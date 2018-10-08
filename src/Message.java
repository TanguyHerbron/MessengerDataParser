import java.util.Date;

public class Message {

    private String sender;
    private long time;
    private String text;
    private int nbReact;
    private int nbFile;

    public Message(String sender, long time, String text, int nbReact, int nbFile)
    {
        this.sender = sender;
        this.time = time;
        this.text = text;
        this.nbReact = nbReact;
        this.nbFile = nbFile;
    }

    public long getDate()
    {
        return time;
    }

    public String getSender()
    {
        return sender;
    }

    public String getText()
    {
        return text;
    }

    public int getNbReact()
    {
        return nbReact;
    }

    public int getNbFile()
    {
        return nbFile;
    }
}
