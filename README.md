# Inverted Indexer built with MapReduce on Hadoop

This inverted indexer simply indexes all word terms (terms with only word-characters and containing min. 1 letter) of plain text files. It builds an index of `term: {docName: occurenceCount}`.

> ℹ The generation of docIDs is omitted as we currently haven't implemented a mapping of those, so we simply return the file names. This is discouraged in general as it is too expensive and unreliable. You could implement a mapping yourself using hadoop's `DistributedCache`.

## Prerequisitions

Edit the `username` in `config.sh` to the one of your `HDFS` path in the form of `/user/$username`. The documents to be indexed must be put into the `documents` directory and will be uploaded to your `HDFS` file system automatically on run.

> ⚠ You have to put hadoop to your `PATH` variable to run the scripts or replace `hadoop` with the path to your hadoop binary installation.

## Run the indexer

You can compile the `InvertedIndexer` class and make a `.jar` out of it by simply running `./compile.sh` in your shell. Afterwards, run it on your hadoop cluster using `./run.sh`. Or, for convenience, do it all together calling `./compile_run.sh`.

The inverted index output files can be found afterwards at `/user/$username/InvertedIndexer/output`.

## Customizing the code

The project does not come with any build tool as it is compiled in the hadoop environment. To improve your coding using IntelliSense and others, simply add the following `.jar`s to your project references, depending on the IDE you use. On VS Code, you can add them to your `.vscode/settings.json` or conveniently using the _Java Projects_ view if you have the corresponding plugin installed.

- `hadoop-3.x.0\share\hadoop\mapreduce\hadoop-mapreduce-client-core-3.3.0.jar`
- `hadoop-3.x.0\share\hadoop\hdfs\hadoop-hdfs-3.3.0.jar`
- `hadoop-3.x.0\share\hadoop\common\hadoop-common-3.3.0.jar`