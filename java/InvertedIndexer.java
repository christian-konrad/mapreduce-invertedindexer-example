import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertedIndexer {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{
    private static final String PATTERN = "[\\p{L}0-9]*\\p{L}[\\p{L}0-9]";
    private Text term = new Text();
    private Text docName = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      // get the name of the document as the key; 
      // remove whitespaces as they are used as docName list delimiters
      String docNameString = ((FileSplit) context.getInputSplit()).getPath().getName();
      docName.set(docNameString.replaceAll(" ", "_"));      

      // per every term of the document, emit a tupel of term and document name
      Pattern pattern = Pattern.compile(PATTERN);
      Matcher matcher = pattern.matcher(value.toString());
      while (matcher.find()) {
        term.set(matcher.group(0));
        context.write(term, docName);
      }
    }
  }

  public static class TermsDocIdCombiner
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text term, Iterable<Text> docNames,
                       Context context
                       ) throws IOException, InterruptedException {

      List<String> docNameList = new ArrayList<String>();

      for (Text docName : docNames) {
        String docNameStr = docName.toString();
        docNameList.add(docNameStr);
      }
    	
      result.set(String.join(" ", docNameList));
      context.write(term, result);
    }
  }

  public static class PostingsListReducer
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text term, Iterable<Text> docNameLists,
                       Context context
                       ) throws IOException, InterruptedException {
      Map<String, Integer> docNameOccurences = new TreeMap<String, Integer>();

      for (Text docNameList : docNameLists) {
        // docName could be a list, because of the combiner
        String[] docNameArray = docNameList.toString().split(" ");

        for (String docName : docNameArray) {
          docNameOccurences.merge(docName, 1, (prev, one) -> prev + one);  
        }
      }

      String serializedOccurences = docNameOccurences.entrySet().stream().map(
        docNameOccurence -> docNameOccurence.getKey() + ":" + docNameOccurence.getValue()
      ).collect(Collectors.joining(" "));

      result.set(serializedOccurences);
      context.write(term, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "inverted indexer");
    job.setJarByClass(InvertedIndexer.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(TermsDocIdCombiner.class);
    job.setReducerClass(PostingsListReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}