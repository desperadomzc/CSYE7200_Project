import org.opencv.core.Core;

public class Test {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        HelloOpencv h = new HelloOpencv();
        ImageUtils iu = new ImageUtils("images\\cut2.jpg");
        System.out.print(h.getRowNum(iu));
        System.out.print(h.getColNum(iu));
    }
}
