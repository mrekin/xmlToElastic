# xmlToElastic

Simple batch tool to load set of .xml files to Elasticsearch.
xmlToElastic uses **JSON** library to convert xml to json and **elasticsearch-rest-high-level-client** to load result to Elastic.

Download *fat* jar, run it.
*Use **-h** option to see avaliable parameters.*

TODO list:
* **Debug mode** 
* ~~xml to json with json saving to check conversion result~~
* **iD increment mode** to generate ID for elastic as increment from start value
* Batch json loading (result from xml2json conversion)
* Normal Logging, output, error handling and other good things :)

Updates:
- ver. **0.3.0** :
  - --**xml2json** option added. It allows to save resulting JSON to output directory
  - Some code and output fixes
