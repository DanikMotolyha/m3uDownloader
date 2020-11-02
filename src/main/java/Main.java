import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input m3u file path");
        String path = scanner.nextLine();
        System.out.println("Input output directory path");
        String outPutPath = scanner.nextLine();

        System.out.println("Scanning m3u file . . .");
        Collection<Song> songs = new LinkedList<>();
        try (Scanner m3uScanner = new Scanner(new File(path))) {
            m3uScanner.nextLine();
            while (m3uScanner.hasNextLine()) {
                Song song = new Song();
                String name = m3uScanner.nextLine().split(",")[1].trim()
                        .replaceAll("[>,<:*?!%@/.'\"|+\\\\]", "_");
                song.setName(name);
                song.setLink(m3uScanner.nextLine());
                songs.add(song);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Done!");
        System.out.print("Downloading your music . . .\r");
        int i = 0;
        for (Song song : songs) {
            if(new File(String.format("%s\\%s.mp3", outPutPath, song.getName())).isFile()){
                ++i;
                continue;
            }
            System.out.print("Downloading your music . . . (" + i++ + "/" + songs.size() + ")\r");
            URL url = new URL(song.getLink());
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(String
                    .format("%s\\%s.mp3", outPutPath, song.getName()));
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            readableByteChannel.close();
        }
        System.out.println("Done!");
    }

}

class Song {
    String name;
    String link;

    @Override
    public String toString() {
        return name + "\n" + link + "\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
