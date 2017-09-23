import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by flengron on 23/09/2017.
 */
public class XspeedIt {
    public final static int  BOX_SIZE = 10;

    public static class PackingLine {
        private final static int MAX_UNPACKED_BOX_NB = 9;
        private int[] packetSizeNb;
        private long[] powerOf10;
        private int boxSize;
        private int unpackedPacketNb = 0;
        private StringBuilder boxesResult;

        PackingLine(int boxSize) {
            packetSizeNb = new int[boxSize];
            powerOf10 = new long[boxSize];
            this.boxSize = boxSize;
            boxesResult = new StringBuilder();
            for (int index = 0; index < boxSize; index++) {
                powerOf10[index] = (long) Math.pow(10, index);
            }
        }

        /**
         * Add a packet to the packing line
         * @param packetSize size of the packet to add
         */
        public void addPacket(int packetSize) {
            if (packetSize >= BOX_SIZE) return;
            unpackedPacketNb++;

            // try to find a pair of packets to fill the box
            if (hasComplementPacket(packetSize)) {
                sendBox(packetSize, BOX_SIZE - packetSize);
                packetSizeNb[packetSize]++;
            } else {
                // otherwise keep it for later use
                packetSizeNb[packetSize]++;
                if (unpackedPacketNb >= MAX_UNPACKED_BOX_NB) {
                    //try to fill a box with one or more than 2 packets
                    fillBox();
                }
            }
        }

        /**
         * Put all the current packets into some boxes
         */
        public void fillAllBoxes() {
            while (unpackedPacketNb > 0) {
                fillBox();
            }
        }

        /**
         *
         * @return the string of the current filled boxes
         */
        public String getResult() {
            return boxesResult.toString();
        }

        private class PacketCombination {
            int sum; // use to store the current sum of the packets list
            long packetSizeleft; // use the store the remaining packets size
            int currentValue; // use the store the current combination

            PacketCombination(int sum, long packetSizeleft, int currentValue) {
                this.sum = sum;
                this.packetSizeleft = packetSizeleft;
                this.currentValue = currentValue;
            }
        }

        private void fillBox() {
            long packetSizeNbValue = 0;
            for (int index = 1; index < boxSize; index++) {
                packetSizeNbValue += packetSizeNb[index] * powerOf10[index];
            }

            // use a queue to add all the combinations between packets
            Queue<PacketCombination> queue = new LinkedList<>();
            for (int index = 1; index < boxSize; index++) {
                if (packetSizeNb[index] > 0) {
                    queue.add(new PacketCombination(index, packetSizeNbValue - powerOf10[index], index));
                }
            }

            int foundValue = -1;
            int bestValue = 0;
            int bestSum = 0;
            while ((!queue.isEmpty()) && (foundValue == -1)) {
                PacketCombination combination = queue.poll();
                for (int index = 1; index < boxSize; index++) {
                    if (((combination.packetSizeleft / powerOf10[index]) % 10) > 0) {
                        int sum = combination.sum + index;
                        if (sum > boxSize) break;

                        int currentValue = combination.currentValue * 10 + index;
                        if (sum == boxSize) {
                            foundValue = currentValue;
                        } else {
                            long currentPacketSizeNbValue = combination.packetSizeleft - powerOf10[index];
                            queue.add(new PacketCombination(sum, currentPacketSizeNbValue, currentValue));
                            if (bestSum < sum) {
                                bestSum = sum;
                                bestValue = currentValue;
                            }
                        }
                    }
                }
                if (bestSum < combination.sum) {
                    bestSum = combination.sum;
                    bestValue = combination.currentValue;
                }
            }

            if (foundValue == -1) {
                foundValue = bestValue;
            }

            sendBox(foundValue);
        }

        private boolean hasComplementPacket(int packetSize) {
            return packetSizeNb[boxSize - packetSize] > 0;
        }

        private void sendBox(int packets) {
            while (packets != 0) {
                int currentIndex = packets % 10;
                boxesResult.append(currentIndex);
                packetSizeNb[currentIndex]--;
                unpackedPacketNb--;
                packets /= 10;
            }
            boxesResult.append('/');
        }

        private void sendBox(int... packets) {
            for (int packet : packets) {
                boxesResult.append(packet);
                packetSizeNb[packet]--;
            }
            boxesResult.append('/');
            unpackedPacketNb -= packets.length;
        }
    }

    public static void main(String[] args) {
        String boxes = "163841689525773";

        PackingLine packingLine = new PackingLine(BOX_SIZE);

        for(int index=0; index<boxes.length(); index++) {
            packingLine.addPacket(Character.getNumericValue(boxes.charAt(index)));
        }

        packingLine.fillAllBoxes();
        System.out.println(packingLine.getResult());
    }
}
