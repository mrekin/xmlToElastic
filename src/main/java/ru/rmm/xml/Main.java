package ru.rmm.xml;

import com.beust.jcommander.JCommander;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;
import org.json.XML;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {

    static BulkRequest br = null;

    public static void main(String[] args) {
        try {


            Args arg = new Args();
            JCommander jcommander = new JCommander(arg);
            jcommander.setProgramName("xml2elastic");
            jcommander.parse(args);

            if(arg.isHelp()){
                jcommander.usage();
                return;
            }

            String dir = arg.getDirectory();
            Stream<Path> files = Files.list(Paths.get(dir));
            System.out.println("Starting..");
            System.out.println("Working folder: " + dir);
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(
                            HttpHost.create(arg.getServer())));

            if(arg.isBulk())
                {br = new BulkRequest();}

            Consumer<Path> converter = x -> {
                try {
                    System.out.println("File: " + x.toString());
                    if (!x.toString().endsWith(".xml")) {
                        return;
                    }
                    String id = "";
                    if(arg.getIdMode()==1)  {id = x.toString().split("\\.")[0];}

                    JSONObject jo = XML.toJSONObject(new FileReader(x.getFileName().toFile()), arg.isKeepStrings());
                    //  System.out.println("JSON is: "+ jo.toString());
                    UpdateRequest request = new UpdateRequest(arg.getIndexName(), arg.getDocumentName(), id);
                    request.doc(jo.toString(), XContentType.JSON);
                    request.validate();

                    if(arg.isBulk()){
                        Main.br.add(request.docAsUpsert(arg.isUpsert()));
                        System.out.println("Bulk request added.");
                    }else{
                        UpdateResponse ur = client.update(request.docAsUpsert(arg.isUpsert()),RequestOptions.DEFAULT);
                        System.out.println("Request executed: "+request.toString());
                    }


                } catch (Exception e) {
                    JCommander.getConsole().println(e.getLocalizedMessage());
                }
            };

            files.forEach(converter);

            if(arg.isBulk()) {
                BulkResponse brs = client.bulk(br,RequestOptions.DEFAULT);
                if(brs.hasFailures()){
                    System.out.println(brs.buildFailureMessage());
                }
                System.out.println("Ended bulk");
            }
            client.close();
            System.out.println("Client closed");
        } catch (Exception ioe) {
            JCommander.getConsole().println(ioe.getLocalizedMessage());
        }
    }

}
