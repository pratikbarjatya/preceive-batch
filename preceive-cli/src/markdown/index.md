# Getting Started

PreCeive Batch is a client for TheySay PreCeive REST API intended for batch processing scenarios.
Using the app via command line, you can analyse and enrich texts from local files with TheySay's PreCeive REST API service without any development work.
The responses from TheySay's analysis service are saved to local files. The app supports combining multiple analyses for an individual text.

## Requirements

You will need Java 8 to run the app.
Download Java [here](https://java.com/en/download/).

## Running the App

### Run Scripts

Once you have installed Java, we recommend that you start by running the example scripts that are provided in the ```bin``` directory.

| Operating System    | Run Script                  |
|---------------------|-----------------------------|
| Linux, Mac OS X     | ```bin/preceive-cli```      |
| Windows             | ```bin\preceive-cli.bat```  |

For simplicity in the examples below, we use Linux (and Mac OS X) command line commands throughout the documentation.

### PreCeive REST API Credentials

To use the PreCeive REST API service, you will need to input your PreCeive account username and password.
There are currently two ways to submit your credentials.

1) Your credentials can be specified as explicit command line arguments each time you run the app:

	$ bin/preceive-cli -user <username> -password <password> ...

2) Alternatively, you can store your credentials in your environment variables and reuse them later:

**Linux, Mac OS X**

	export THEYSAY_USER=<username>
	export THEYSAY_PASSWORD=<password>
	bin/preceive-cli ...

**Windows**

	SET THEYSAY_USER=<username>
	SET THEYSAY_PASSWORD=<password>
	bin\preceive-cli.bat ...

### Example Scripts

The app ships with an example script which illustrate how to submit data from local files to PreCeive REST API.

To run the example script (```bin/getting_started.sh```):

	$ bin/preceive-cli -user <username> -password <password> -output results/results.json -endpoints /v1/sentiment -batch corpus/getting_started.json corpus/getting_started.tsv corpus/getting_started.json

This command analyses the contents of three example texts by calling the http://api.theysay.io/v1/sentiment endpoint.
The results - i.e. the responses from the PreCeive REST API - will be appended to ```results/results.json``` file.

### Input Data

#### JSON

The app currently accepts JSON input data from a file which needs to contain strictly one (1) JSON object per line.
The app assumes a JSON read mode with any input file which has ```.json``` as its file extension.

Your JSON input data could look like this:

	{ "id": 1, "text": "Today is a great day." }
	{ "id": 5, "text": "Today is a nasty day." }

For an example, see ```corpus/getting_started.json```.

#### TSV

The app currently accepts tab-separated input data from a file which needs to contain strictly one (1) text per line, with all newlines escaped.
The default TSV newline escape sequence is `\n` but this can changed using the `-tsv-newline` command line option.
The app assumes a TSV read mode with any input file which has ```.tsv``` as its file extension.

Your TSV input data could look like this:

	1 \t Today is a great day.
	5 \t Today is a nasty day.

For an example, see ```corpus/getting_started.tsv```.

#### Excel

The app currently offers minimum support for reading XLSX files.
The app opens the **first sheet** in an XLSX file and finds the **header row**.
It then reads each row and submits each text cell to the PreCeive REST API.

For an example, see ```corpus/getting_started.xlsx```.

### Text IDs

For each text (cf. line or cell), an optional `id` field can be specified.
The `id`, which will be carried throughout the processing flow, makes it easier to align texts downstream.
If no `id` is specified, an auto-generated value is derived with the `filename@linenumber` pattern as follows:

```
"id": "corpus/getting_started.json@00000002"
```

### Output Data

Currently only ```json``` output is supported.
The output file (to which the app appends the responses from PreCeive REST API) contains one (1) JSON response object per line.
For detailed information about the fields and structure of each JSON object, please consult [TheySay PreCeive REST API Documentation](http://docs.theysay.apiary.io).

### Endpoints

The ```-endpoints``` option specifies the analyses that are required to be performed on the input data. 
It takes the form of ```<enpoint url>``` for example ```/v1/sentiment```. 


#### Adding Parameters

Additional parameters to the endpoint can be provided by append '?' followed by key=value pairs for example, ```/v1/sentiment?level=sentence```.
The keys and values are processed such that ```/v1/sentiment?bias.positive=2&bias.negative=1&bias.neutral=0.5``` allows the specifying of the sentiments bias settings as described in the API documentation.


#### Wrapper Fields for Multi-endpoint Calls

The app allows you to submit a single piece of text to multiple PreCeive REST API analysis endpoints.
The `-endpoints` will accept multiple string arguments each will be called for each text and collected in the result.

Since calls to multiple endpoints generate multiple responses, you can specify wrapper fields for the output data to organise, align, and manage the responses received. For example, you could run both document- and sentence-level sentiment analysis and store their responses in two separate containers in the final JSON output (e.g. `sentiment.document` for the former and `sentiment.sentence` for the latter) with the following `-endpoints` arguments:

```
-endpoints sentiment.document=/v1/sentiment?level=document sentiment.sentence=/v1/sentiment?level=sentence  
```

The `-endpoints` command line option takes multiple space-separated arguments. The format is simply `wrapper=endpoint` for each endpoint definition.

By default, the response data from PreCeive REST API is wrapped inside a `response` container field.
For example, an `-endpoints` definition such as `/v1/sentiment?level=sentence` that lacks an explicit wrapper field name will be output as


	{
		"id":"0123456",
		"response":[{...},{...}]
	}

Correspondingly, an `-endpoints` definition with the explicit wrapper field `foo=/v1/sentiment?level=sentence` will get rendered as


	{
		"id": "0123456",
		"foo": [{...},{...}]
	}


while the composite definition ` sentiment.holistic=/v1/sentiment?level=document sentiment.detailed=/v1/sentiment?level=sentence` would yield


	{
		"id":"0123456",
		"sentiment":{
			"detailed":[{...},{...}]
			"holistic":{}
		}
	}


#### Text IDs

The output data also includes the ID specified for each input text (see above).


#### Errors

If a response from PreCeive REST API contains any errors, they are output in the same field as a successful response.
For example,


	{
		sentiment:{
			"errors":[
			  { "message":"Text must be more than 10" }
			]
		}
	}

## Command Line Options

The full list of command line options for the app can be displayed with the `-help` argument:

	bin/preceive-cli -help

The options are also listed in [./commandline.html](./commandline.html).
