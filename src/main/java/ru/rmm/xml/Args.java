package ru.rmm.xml;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names ={"-h", "--help","--usage","/?"}, help = true,  order = 0)
    private boolean help;

    private String helpMessage = "";
    // TBD need to implement
    /*
    @Parameter(names = "--xml2json", description = "Only converts xml to json and saves it in source directory",  order = 1)
    private boolean xml2json = false;
    */
    @Parameter(names = "--keepStrings", description = "Keepstring key for Json generation",  order = 2)
    private boolean keepStrings = true;

    @Parameter(names = "--xml2elastic", description = "Converts xml to Json and PUT result to Elastic server",  order = 3)
    private boolean xml2elastic = false;

    @Parameter(names = {"-d","--directory"}, description = "XML source directory",  order = 4)
    private String directory = ".";

    @Parameter(names = "--server", description = "Elactic server URI ",  order = 5)
    private String server = "http://localhost:9200";

    @Parameter(names = {"--bulk"}, description = "Enable/disable bulk mode for UPDATE. Bulk faster but will take more memory",  order = 6)
    private boolean bulk = false;

    @Parameter(names = {"--indexName"}, description = "Elastic index name",  order = 7)
    private String indexName = "index";

    @Parameter(names = {"--documentName"}, description = "Elastic document name",  order = 8)
    private String documentName = "_doc";

    // TBD Need to add increment mode
    @Parameter(names = {"--idMode"}, description = "1 - fileName mode (for file 123.xml id will be 123, 2 - emptyId, let Elastic generate ID",  order = 9)
    private Integer  idMode = 2;

    @Parameter(names = "--upsert", description = "Upsert mode (update + insert)",  order = 10)
    private boolean upsert = true;

// TBD nee to implement
    @Parameter(names = "--debug", description = "Debug mode",  order = 11)
    private boolean debug = false;


    public List<String> getParameters() {
        return parameters;
    }

    public boolean isHelp() {
        return help;
    }

    //TBD need to implement
   /* public boolean getXml2json() {
        return xml2json;
    }
    */
    public boolean isXml2elastic() {
        return xml2elastic;
    }

    public String getDirectory() {
        return directory;
    }

    public String getServer() {
        return server;
    }

    public boolean isBulk() {
        return bulk;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Integer getIdMode() {
        return idMode;
    }
/*
    public boolean isDebug() {
        return debug;
    }
*/
    public boolean isKeepStrings() {
        return keepStrings;
    }

    public boolean isUpsert() {
        return upsert;
    }
}