public class Main {
    public static void main(String[] args) {
        ListInformationSender lis = new ListInformationSender();
        lis.start();
        PositionReceiver pr = new PositionReceiver();
        pr.start();
        PositionsSender ps = new PositionsSender();
        ps.start();
    }
}