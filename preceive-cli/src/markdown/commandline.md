#Command Line Options 
The full list of command line options can be displayed using

	bin/preceive-cli -help

### Currently available options

	-help
		Display the help and usage information for this app.

	-user
		A PreCeive REST API username.

	-password
		A PreCeive REST API password.

	-service
		The PreCeive REST API service to use.

	-threads
		The number of concurrent threads/connections during batch processing.

	-backlog
		The maximum number of items to be held in memory during batch processing.

	-endpoints
		The PreCeive REST API endpoints to call.
		Each endpoint is of the form [field in result json]=[api path][?level=document|sentiment]
		e.g. -endpoints document.sentiment=/v1/sentiment?level=document sentence.sentiment=v1/sentiment?level=sentence

	-copy-input-data-as
		The field under which to store the original submitted data in the result. 

	-input-text
		The field in the input data that contains the core text content to be processed.

	-input-id
		The field in the input data that represents the id of each text. By default, an auto-generated ID will be used.

	-tsv-newline
		Replace each occurrence of the specified string with a newline character.

	-batch
		Process the files provided as arguments consistent with the previously specified options.
		The results will be save to the file specified using the -output option.
		 e.g. ... -batch <file1> <file2> <file3>

	-process-documents
		Process each provided document one at a time and write the results for each document to a separate file.
		-output value is used as a template inserting the current inputfile name before the extension.
		For example for texts.tsv -output results/output.json -> results/output.texts.tsv.json.
		The extension is included to ensure that the results for x.tsv and x.json do not overwrite each other.
		A good example would be -output results/.json -> results/texts.tsv.json

	-document-analysis
		Perform a standard analysis of document emotions,sentiment,topics.
		 e.g. preceive-cli -output results.json -document-analysis file1 file2 file3

	-output
		The file to which the responses will be saved. Use the suffix `json.gz` to gzip the output file.

	-max-records-per-file
		Maximum number of records to write to an individual file. (< 0 is unlimited)

