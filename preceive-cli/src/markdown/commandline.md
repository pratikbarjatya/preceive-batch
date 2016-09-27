# Command Line Options

The full list of command line options for the app can be displayed with the `-help` argument:

	bin/preceive-cli -help


The following options are currently available:

-help
: Display the help and usage information for this app.

-user
: A PreCeive REST API username.

-password
: A PreCeive REST API password.

-service
: The PreCeive REST API service to use.

-threads
: The number of concurrent threads (connections) to be used for a batch processing run.

-backlog
: The maximum number of raw texts to be held in memory during a batch processing run.

-endpoints
: The TheySay PreCeive REST API endpoints to call.
: Each endpoint is of the form [field in output JSON]=[api path][?level=document|sentiment]
: Example:  -endpoints document.sentiment=/v1/sentiment?level=document sentence.sentiment=v1/sentiment?level=sentence

-copy-input-data-as
: The field under which to store the original submitted data in the result.

-input-text
: The field in the input data which contains the core text content that you want to process.

-input-id
: The field in the input data that represents the ID of each text.
: By default, an auto-generated ID is used.

-tsv-newline
: An escape string for input texts that span multiple lines.
: All occurrences of the specified string in the input data will be converted into a newline character ('\n').
: Example:  @@NEWLINE@@

-batch
: The input data files to be processed in a batch processing run.
: The file names need to be separated by a space character.
: Example:  ... -batch <file1> <file2> <file3>

-process-documents
: Process each input document one at a time and write the results for each document to a separate output file.
: -output value is used as a template inserting the current inputfile name before the extension.
: For example for texts.tsv -output results/output.json -> results/output.texts.tsv.json.
: The extension is included to ensure that the results for x.tsv and x.json do not overwrite each other.
: A good example would be -output results/.json -> results/texts.tsv.json

-document-analysis
: Perform a standard analysis of document emotions,sentiment,topics.
:  e.g. preceive-cli -output results.json -document-analysis file1 file2 file3

-output
: The file to which the responses from TheySay's PreCeive REST API will be saved.
: Use the suffix `json.gz` to gzip the output file.

-max-records-per-file
: The maximum number of response objects to write to an individual output file.
: For an unlimited number, use a negative value (< 0).
