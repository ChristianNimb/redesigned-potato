import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/** Builds the offline game. Directions follow the board: north, east, south, west. */
public class BeaconFold {
    record Pose(int x, int y, int width, int depth) {
        int height() { return 2 / (width * depth); }
        Pose roll(int direction) {
            int h = height();
            return switch (direction) {
                case 0 -> new Pose(x, y-h, width, h);
                case 1 -> new Pose(x+width, y, h, depth);
                case 2 -> new Pose(x, y+depth, width, h);
                case 3 -> new Pose(x-h, y, h, depth);
                default -> throw new IllegalArgumentException();
            };
        }
    }
    record Island(String name, String[] rows) {
        boolean supported(Pose p) {
            for (int y=p.y; y<p.y+p.depth; y++)
                for (int x=p.x; x<p.x+p.width; x++)
                    if (y<0 || y>=rows.length || x<0 || x>=rows[y].length() || rows[y].charAt(x)=='.') return false;
            return true;
        }
        Pose marker(char c) {
            for (int y=0; y<rows.length; y++) {
                int x=rows[y].indexOf(c);
                if (x>=0) return new Pose(x,y,1,1);
            }
            throw new IllegalArgumentException("Missing marker "+c);
        }
        String compile() {
            Pose goal=marker('G');
            var poses=new ArrayList<Pose>();
            var ids=new HashMap<Pose,Integer>();
            var dist=new ArrayList<Integer>();
            poses.add(goal); ids.put(goal,0); dist.add(0);
            for (int i=0; i<poses.size(); i++) {
                for (int d=0; d<4; d++) {
                    Pose next=poses.get(i).roll(d);
                    if (supported(next) && !ids.containsKey(next)) {
                        ids.put(next,poses.size()); poses.add(next); dist.add(dist.get(i)+1);
                    }
                }
            }
            Integer start=ids.get(marker('S'));
            if (start==null) throw new IllegalStateException("Unsolvable island "+name);
            var states=new StringJoiner(",","[","]");
            for (int i=0; i<poses.size(); i++) {
                Pose p=poses.get(i);
                var moves=new int[4];
                for (int d=0; d<4; d++) moves[d]=ids.getOrDefault(p.roll(d),-1);
                states.add("["+p.x+","+p.y+","+p.width+","+p.depth+","+dist.get(i)+","+Arrays.toString(moves)+"]");
            }
            System.out.println(name+" : "+dist.get(start)+" moves, "+poses.size()+" reachable poses");
            return "{\"name\":\""+name+"\",\"rows\":["+Arrays.stream(rows).map(r->"\""+r+"\"").collect(Collectors.joining(","))+"],\"start\":"+start+",\"states\":"+states+"}";
        }
    }
    static Island island(String name, String... rows) { return new Island(name, rows); }
    public static void main(String[] args) throws Exception {
        var levels=List.of(
            island("First Light",".......","##S####","#######","#####G#","..####."),
            island("The Narrows","###....","S######","#######","....###","...####","...#G##"),
            island("Salt Steps","..#####..","..##.##..","S###.####","####..###",".###..###","......G##"),
            island("Broken Crown","..#####..",".##...##.","###...###","S##...#G#","###...###",".#######.","..##.##.."),
            island("Last Lantern","###...###","S########","#####.#G#","....##.##","..####.##","..##.####","..#######"));
        String data=levels.stream().map(Island::compile).collect(Collectors.joining(",","[","]"));
        Files.createDirectories(Path.of("dist"));
        Files.writeString(Path.of("dist","index.html"),Files.readString(Path.of("game.template.html")).replace("/*ISLAND_DATA*/",data));
    }
}

