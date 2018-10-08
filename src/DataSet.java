import javax.xml.crypto.Data;

public class DataSet {

    private int nbMessage;
    private int nbCharacter;
    private int nbReact;
    private int nbFile;

    public DataSet(int nbMessage, int nbCharacter, int nbReact, int nbFile)
    {
        this.nbMessage = nbMessage;
        this.nbCharacter = nbCharacter;
        this.nbReact = nbReact;
        this.nbFile = nbFile;
    }

    public int getNbMessage()
    {
        return nbMessage;
    }

    public int getNbCharacter()
    {
        return nbCharacter;
    }

    public int getNbReact()
    {
        return nbReact;
    }

    public int getNbFile()
    {
        return nbFile;
    }

    @Override
    public String toString()
    {
        String str = " messages : " + nbMessage + " | characters : " + nbCharacter;

        if(nbReact > 0)
        {
            str += " | reactions : " + nbReact;
        }

        if(nbFile > 0)
        {
            str += " | files : " + nbFile;
        }

        return str;
    }
}
