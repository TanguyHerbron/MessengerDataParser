import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.xml.crypto.Data;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Worker {

    public static void main(String[] args) throws ParseException
    {
        File file = new File("FILE PATH");
        List<Message> sentMessage = new ArrayList<>();
        List<Message> receivedMessage = new ArrayList<>();

        HashMap<String, DataSet> sentMessageDateCounter = new HashMap<>();
        HashMap<String, DataSet> receivedMessageDateCounter = new HashMap<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            //06/09/2018 00:34:06
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line = null;
            String completeFile = "";

            while((line = bufferedReader.readLine()) != null)
            {
                completeFile += line;
            }

            Document doc = Jsoup.parse(completeFile);
            Element body = doc.body();
            Element container = body.selectFirst("div");

            for(int i = 2; i < container.children().size(); i++) {
                Element masterDiv = container.child(i);

                if(masterDiv.children().size() > 0)
                {
                    Element secondDiv = masterDiv.child(0);

                    if(secondDiv.className().equals("clearfix"))
                    {
                        if(secondDiv.children().size() > 0)
                        {
                            /*if(secondDiv.child(0).className())*/
                            Element outerDiv = secondDiv.child(0);

                            if(outerDiv.className().equals("avatar"))
                            {
                                outerDiv = secondDiv.child(1);
                            }

                            if(outerDiv.children().size() > 0)
                            {
                                Element divElement = outerDiv.child(0);

                                Element messageSpan = divElement.child(0);

                                int nbReact = 0;

                                String sender = divElement.toString().substring(divElement.toString().indexOf("title=\"") + 7);
                                sender = sender.substring(0, sender.indexOf("\""));

                                String dateStr = divElement.toString().substring(divElement.toString().indexOf("time=\"") + 6);
                                dateStr = dateStr.substring(0, dateStr.indexOf("\""));

                                Date date = sdf.parse(dateStr);

                                String message = messageSpan.toString().substring(messageSpan.toString().indexOf(">") + 1);
                                message = message.substring(0, message.indexOf("</span"));

                                String side = divElement.toString().substring(divElement.toString().indexOf("class=\"") + 7);
                                side = side.substring(0, side.indexOf("\""));

                                if(outerDiv.children().size() > 1)
                                {
                                    nbReact++;
                                }

                                if(side.equals("box_r"))
                                {
                                    sentMessage.add(new Message(sender, date.getTime()
                                            , message, nbReact, divElement.children().size() - 1));
                                }
                                else
                                {
                                    receivedMessage.add(new Message(sender, date.getTime()
                                            , message, nbReact, divElement.children().size() - 1));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Sent : " + sentMessage.size());
            System.out.println("Received : " + receivedMessage.size());

            computeMessagesPerDay(sentMessage, sentMessageDateCounter);
            computeMessagesPerDay(receivedMessage, receivedMessageDateCounter);

            JSONArray jsonArray = new JSONArray();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (Map.Entry<String, DataSet> entry : sentMessageDateCounter.entrySet()) {
                DataSet dataSetSent = sentMessageDateCounter.get(entry.getKey());
                DataSet dataSetReceived = receivedMessageDateCounter.get(entry.getKey());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("category", entry.getKey());
                jsonObject.put("column-1", dataSetSent.getNbMessage());
                jsonObject.put("column-2", dataSetReceived.getNbMessage());

                jsonArray.put(jsonObject);
            }

            System.out.println(jsonArray);

            //Mon Sep 10 00:00:00 CEST 2018
        }
    }

    private static void computeMessagesPerDay(List<Message> messages, HashMap<String, DataSet> dayDataMap) throws ParseException
    {
        int i = 1;

        Date referenceDate = getReferenceDate(messages.get(0).getDate());

        int counterDate = 1;
        int characterCounter = messages.get(0).getText().length();
        int reactCounter = messages.get(0).getNbReact();
        int fileCounter = messages.get(0).getNbFile();

        while(messages.size() > i)
        {
            if(getDifferenceDays(referenceDate.getTime(), messages.get(i).getDate()) >= 1)
            {
                dayDataMap.put(referenceDate.toString(), new DataSet(counterDate, characterCounter, reactCounter, fileCounter));
                counterDate = 1;
                characterCounter = messages.get(i).getText().length();
                referenceDate = getReferenceDate(messages.get(i).getDate());
                reactCounter = messages.get(i).getNbReact();
                fileCounter = messages.get(i).getNbFile();
            }
            else
            {
                counterDate++;
                characterCounter += messages.get(i).getText().length();
                reactCounter += messages.get(i).getNbReact();
                fileCounter += messages.get(i).getNbFile();
            }

            i++;
        }

        dayDataMap.put(referenceDate.toString(), new DataSet(counterDate, characterCounter, reactCounter, fileCounter));
    }

    private static Date getReferenceDate(long time) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.parse(sdf.format(time));
    }

    private static float getDifferenceDays(long time1, long time2)
    {
        float daysBetween = -1;

        try {
            long difference = time2 - time1;
            daysBetween = ((float) difference / (1000*60*60*24));
            /* You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysBetween;
    }

}
