package bricks.util;
import java.util.*;

public class LogicalPacket {

    public double packetSize;
    public double interarrivalTimeOfPackets;

    public String usage() {
	return "Packet <double logicalPacketSize>\n";
    }

    public void set(String str, String line) throws BricksParseException {
	if (!str.equalsIgnoreCase("packet"))
	    throw new BricksParseException(
		"The first line has to be a packet declaration.\n" + usage()
	    );
	parsePacketLine(line);
    }

    private void parsePacketLine(String line) throws BricksParseException {
	try {
	    StringTokenizer st = new StringTokenizer(line);
	    String tmp = st.nextToken(" \t"); // packet
	    packetSize = Double.valueOf(st.nextToken(" \t")).doubleValue();

	} catch (NoSuchElementException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());

	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new BricksParseException(usage());
	}
    }
}
