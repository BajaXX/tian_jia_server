
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class text {

    public static void main(String[] args) {
        String[] regulation = {"诸葛亮","鲁班","貂蝉","吕布"};
        final List<String> regulationOrder = Arrays.asList(regulation);
        String[] ordered = {"貂蝉","诸葛亮","吕布","貂蝉","鲁班","诸葛亮","貂蝉","鲁班","诸葛亮"};
        List<String> orderedList = Arrays.asList(ordered);
        Collections.sort(orderedList, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                int io1 = regulationOrder.indexOf(o1);
                int io2 = regulationOrder.indexOf(o2);
                return io1 - io2;
            }
        });
        System.out.println(orderedList);
    }
}
