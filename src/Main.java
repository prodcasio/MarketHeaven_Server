import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Inizializzo i valori del file assets.xml
        FileWriter writer = new FileWriter("assets.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<assets>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>EURUSD</name>\n" +
                "        <value>1.094037</value>\n" +
                "        <spread>1</spread>\n" +
                "        <change>-2.62</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>GBPUSD</name>\n" +
                "        <value>1.377219</value>\n" +
                "        <spread>9</spread>\n" +
                "        <change>2.34</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>USDJPY</name>\n" +
                "        <value>109.187866</value>\n" +
                "        <spread>1</spread>\n" +
                "        <change>-1.62</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>AUDUSD</name>\n" +
                "        <value>0.668216</value>\n" +
                "        <spread>2</spread>\n" +
                "        <change>-1.57</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>USDCAD</name>\n" +
                "        <value>1.203665</value>\n" +
                "        <spread>8</spread>\n" +
                "        <change>-2.5</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>EURGBP</name>\n" +
                "        <value>0.844399</value>\n" +
                "        <spread>2</spread>\n" +
                "        <change>1.05</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>USDCHF</name>\n" +
                "        <value>0.957644</value>\n" +
                "        <spread>6</spread>\n" +
                "        <change>-3.04</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>NZDUSD</name>\n" +
                "        <value>0.724712</value>\n" +
                "        <spread>2</spread>\n" +
                "        <change>-1.34</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>EURJPY</name>\n" +
                "        <value>120.583620</value>\n" +
                "        <spread>3</spread>\n" +
                "        <change>-2.33</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>GBPJPY</name>\n" +
                "        <value>149.465977</value>\n" +
                "        <spread>4</spread>\n" +
                "        <change>0.4</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>AUDJPY</name>\n" +
                "        <value>81.694938</value>\n" +
                "        <spread>0</spread>\n" +
                "        <change>0.57</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>CADJPY</name>\n" +
                "        <value>89.491856</value>\n" +
                "        <spread>0</spread>\n" +
                "        <change>-0.08</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>CHFJPY</name>\n" +
                "        <value>115.034351</value>\n" +
                "        <spread>8</spread>\n" +
                "        <change>2.39</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>EURAUD</name>\n" +
                "        <value>1.608853</value>\n" +
                "        <spread>9</spread>\n" +
                "        <change>-2.75</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>GBPAUD</name>\n" +
                "        <value>2.019897</value>\n" +
                "        <spread>3</spread>\n" +
                "        <change>1.62</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>AUDNZD</name>\n" +
                "        <value>1.355136</value>\n" +
                "        <spread>0</spread>\n" +
                "        <change>0.7</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>GBPNZD</name>\n" +
                "        <value>1.219736</value>\n" +
                "        <spread>4</spread>\n" +
                "        <change>-1.2</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>NZDCAD</name>\n" +
                "        <value>0.848816</value>\n" +
                "        <spread>7</spread>\n" +
                "        <change>0.38</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>EURNZD</name>\n" +
                "        <value>1.146610</value>\n" +
                "        <spread>0</spread>\n" +
                "        <change>2.06</change>\n" +
                "    </asset>\n" +
                "    <asset childof=\"forex\">\n" +
                "        <name>NZDCHF</name>\n" +
                "        <value>0.739894</value>\n" +
                "        <spread>7</spread>\n" +
                "        <change>0.73</change>\n" +
                "    </asset>\n" +
                "</assets>");
        writer.close();
        ListInformationSender lis = new ListInformationSender();
        lis.start();
        PositionReceiver pr = new PositionReceiver();
        pr.start();
        PositionsSender ps = new PositionsSender();
        ps.start();
    }
}