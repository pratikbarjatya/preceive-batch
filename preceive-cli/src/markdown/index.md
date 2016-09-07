# Getting Started

PreCeive Batch is a TheySay PreCeive API client that provides batch processing features. 
Using the command line tools, it possible to read texts from files and then analyse them using the PreCeive API.
The results are saved to files. In addition, it supports combining multiple analyses for an individual text.
 
## Requirements

You will need Java 8 to run the app. 
Download Java [here](https://java.com/en/download/).

## Running the App

### First Steps

First extract the contents of the zip (or tar) archive, then navigate to the directory that was generated from the archive.
For simplicity in the examples below, we use as examples Linux and Mac OS X command line commands throughout the documentation.  

### Run Scripts

Once you have installed Java, we recommend that you run the example scripts that are provided in the ```bin``` directory.

| Operating System | Run Script                         |
|------------------------------|----------------------------------------|
| **Linux, Mac OS X**   | ```bin/preceive-cli```         |
| **Windows**               | ```bin\preceive-cli.bat```  |

### PreCeive REST API Credentials

To use the PreCeive REST API, you will need your account username and password.
There are currently two ways to submit your credentials, either (i) as explicit command line arguments or (ii) as stored environment variables.

Your credentials can be specified as command line arguments each time you run the app:

	$ bin/preceive-cli -user <username> -password <password> ....
	 
Alternatively, you can store your credentials in your environment variables and reuse them later:

**Linux, Mac OS X**

	export THEYSAY_USER=<username>
	export THEYSAY_PASSWORD=<password>
	bin/preceive-cli ....

**Windows**

	SET THEYSAY_USER=<username>
	SET THEYSAY_PASSWORD=<password>
	bin\preceive-cli.bat ....

### Example Scripts

The app ships with a few example scripts which illustrate how to submit sample files to PreCeive REST API.

To run the example:

	$ bin/preceive-cli -user <username> -password <password> -output results/results.json -endpoints /v1/sentiment -batch corpus/getting_started.json corpus/getting_started.tsv corpus/getting_started.json

This command analyses the contents of three example texts using the http://api.theysay.io/v1/sentiment endpoint.
The results will be appended to ```results/results.json```.

### Input Data

##### JSON

The app currently accepts JSON input data from a file which contains strictly one (1) JSON object per line.
The app assumes a JSON read mode with any input file which has ```.json``` as its file extension.

Your JSON input data could look like this:

	{ "id": 1, "text": "Today is a great day." }
	{ "id": 5, "text": "Today is a nasty day." }

For an example, see ```corpus/getting_started.json```.

##### TSV

The app currently accepts tab-separated input data from a file which contains strictly one (1) text per line, with all newlines escaped.
The default TSV newline escape sequence is `\n` but this can changed using the `-tsv-newline` option. 
The app assumes a TSV read mode with any input file which has ```.tsv``` as its file extension.

Your TSV input data could look like this:

	1 \t Today is a great day.
	5 \t Today is a nasty day.

For an example, see ```corpus/getting_started.tsv```.

#### Excel 

The app currently offers minimum support for reading XLSX files.
The app opens the first sheet in an XLSX file and finds the header row.
It then reads each row and submits each text to the PreCeive REST API. 

For an example, see ```corpus/getting_started.xlsx```.

#### Text IDs

For each text (cf. line or cell), an optional `id` field can be specified.
The `id`, which will be carried throughout the processing flow, makes it easier to align texts downstream.
If no `id` is specified, an auto-generated value is derived with the `filename@linenumber` pattern as follows:

```
"id": "corpus/getting_started.json@00000002"
```

### Output Data

Currently only ```json``` output is supported.
The output file contains the responses from PreCeive REST API, with one (1) JSON response object per line.
The fields and structure of each JSON object are described in [TheySay PreCeive API Documentation](http://docs.theysay.apiary.io).


#### Wrapper Fields for Multi-endpoint Calls 

The `-endpoints` string argument can be used to combine multiple PreCeive REST API endpoints.
Since you will receive multiple responses, you can specify the wrapper fields for the resultant output data.
For example,  you could run both document- and sentence-level sentiment analysis and use two wrapper fields for the two responses in the final JSON output - e.g. `sentiment.document` for the former and `sentiment.sentence` for the latter - by listing them as follows:

```
-endpoints sentiment.sentence=/v1/sentiment?level=sentence  sentiment.document=/v1/sentiment?level=document
```

The format is simply `wrapper=endpoint` for each endpoint definition. 
The `-endpoints` option takes multiple space-separated arguments.


However, some additional wrapper fields may be present, depending on the `-endpoints` options used (see above).

By default, the response data is wrapped inside a `response` field.
For example, an `-endpoints` definition such as `/v1/sentiment?level=sentence` that lacks a wrapper field name will be output as


	{
		"id":"0123456",
		"response":[{...},{...}]
	}

Correspondingly, an `-endpoints` definition with a wrapper field such as `myFavouriteField=/v1/sentiment?level=sentence` will get rendered as


	{
		"id": "0123456",
		"myFavouriteField": [{...},{...}]	
	}


while the composite definition `sentiment.details=/v1/sentiment?level=sentence  sentiment.overall=/v1/sentiment?level=document ` generates


	{
		"id":"0123456",
		"sentiment":{
			"details":[{...},{...}]
			"overall":{}
		}
	}
	

#### Text IDs

The output data includes the ID specified for each input text (see above).


#### Notes

If an response contains errors these are included in the same location as a successful reponse. 

## Command Line Reference

The full list of command line options can be displayed using

	bin/preceive-cli -help

This are also shown [here](./commandline.md)


## Notes

Further input formats will be added in subsequent releases.
We aim to support Excel and other outputs in the near future.
