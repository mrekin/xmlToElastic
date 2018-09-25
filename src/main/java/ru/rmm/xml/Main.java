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

import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
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

            if (arg.isHelp()) {
                jcommander.usage();
                return;
            }

            if(".".equals(arg.getOutDirectory())){
                arg.setOutDirectory(arg.getDirectory());
            }

            String dir = arg.getDirectory();
            Stream<Path> files = null;
            try {
                files = Files.list(Paths.get(dir));
            }catch (IOException ioe){
                JCommander.getConsole().println(ioe.toString());
                return;
            }
            System.out.println("Starting..");
            System.out.println("Working folder: " + dir);
            RestHighLevelClient client =  new RestHighLevelClient(
                    RestClient.builder(
                            HttpHost.create(arg.getServer())));;
            if(arg.isXml2elastic()) {
                try {
                    boolean state = client.ping(RequestOptions.DEFAULT);
                } catch (ConnectException ce) {
                    JCommander.getConsole().println("Connect to Elastic failed");
                    return;
                }
            }
            if (arg.isBulk()) {
                br = new BulkRequest();
            }

            Consumer<Path> converter = x -> {
                try {
                    if (!x.toString().endsWith(".xml") && x.toFile().isFile()) {
                        return;
                    }
                    System.out.println("File: " + x.toString());
                    String id = "";
                    if (arg.getIdMode() == 1) {
                        id = x.toString().split("\\.")[0];
                    }

                    JSONObject jo = XML.toJSONObject(new FileReader(x.getFileName().toFile()), arg.isKeepStrings());
                    String outputFileName = "";
                    if(arg.isXml2json()){
                        try {
                            outputFileName  = Paths.get(arg.getOutDirectory()).getFileName() + "/" + x.toFile().getName().replace(".xml", ".json");
                        }catch (Exception ioe){
                        JCommander.getConsole().println(ioe.toString());
                        return;
                    }
                        FileWriter fw = new FileWriter(outputFileName);
                        jo.write(fw);
                        fw.close();
                    }
                    //  System.out.println("JSON is: "+ jo.toString());

                    if(arg.isXml2elastic()) {
                        UpdateRequest request = new UpdateRequest(arg.getIndexName(), arg.getDocumentName(), id);
                        request.doc(jo.toString(), XContentType.JSON);
                        request.validate();
                        if (arg.isBulk()) {
                            Main.br.add(request.docAsUpsert(arg.isUpsert()));
                            System.out.println("Bulk request added.");
                        } else {
                            UpdateResponse ur = client.update(request.docAsUpsert(arg.isUpsert()), RequestOptions.DEFAULT);
                            System.out.println("Request executed: " + request.toString());
                        }
                    }

                } catch (Exception e) {
                    JCommander.getConsole().println(e.getLocalizedMessage());
                }
            };

            files.forEach(converter);

            if (arg.isBulk() && arg.isXml2elastic()) {
                BulkResponse brs = client.bulk(br, RequestOptions.DEFAULT);
                if (brs.hasFailures()) {
                    System.out.println(brs.buildFailureMessage());
                }
                System.out.println("Ended bulk");
            }
            client.close();
            System.out.println("Client closed");
        } catch (Exception ioe) {
            JCommander.getConsole().println(ioe.toString());
        }
    }

}
