import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HelloOpencv {
    private final double[] WHITE = {255.0, 255.0, 255.0};

    public ImageUtils binarize(ImageUtils iu) {
        Mat image = iu.getMat();
        Imgproc.cvtColor(image,image,Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(image,image,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,25,10);
//        Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\img-a.jpg",image);
        return iu;
    }

    public ImageUtils cutImg(ImageUtils iu) {
        int upbound = 400, lowerbound = iu.getHeight();
        Boolean containsWhite;

        //for upper bound:
        for (int i = upbound; i < lowerbound; i++) {
            containsWhite = false;
            for (int j = 0; j < iu.getWidth(); j++) {
                if (iu.getPixel(i, j) == WHITE[0]) {
                    containsWhite = true;
                    break;
                }
            }
            if (!containsWhite) {
                upbound = i;
                break;
            }
        }

        //for lowerbound:
        for (int k = iu.getHeight() - 1; k > upbound; k--) {
            containsWhite = false;
            for (int l = 0; l < iu.getWidth(); l++) {
                if (iu.getPixel(k, l) == WHITE[0]) {
                    containsWhite = true;
                    break;
                }
            }

            if (containsWhite) {
                lowerbound = k;
                break;
            }
        }

        for (int k = lowerbound - 1; k > upbound; k--) {
            containsWhite = false;
            for (int l = 0; l < iu.getWidth(); l++) {
                if (iu.getPixel(k, l) == WHITE[0]) {
                    containsWhite = true;
                    break;
                }
            }
            if (!containsWhite) {
                lowerbound = k;
                break;
            }
        }
        ImageUtils precut = new ImageUtils();
        Rect rect = new Rect(0, upbound, iu.getWidth(), lowerbound - upbound);
        precut.setMat(new Mat(iu.getMat(), rect));

        return precut;
    }

    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        Mat image = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\img-0.jpg");
////        System.out.println(image.channels());
//        Imgproc.cvtColor(image,image,Imgproc.COLOR_BGR2GRAY);
//        Imgproc.adaptiveThreshold(image,image,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV,25,10);
//        Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\img-a.jpg",image);
        HelloOpencv h = new HelloOpencv();
        ImageUtils iu = new ImageUtils("images\\origin.jpg");
        ImageUtils bi = h.binarize(iu);
        bi.writeImg("images\\binarized.jpg");
        h.cutImg(iu).writeImg("images\\cut.jpg");
        /*
        get the RGB number of the color we want.
         */
//        int r = iu.getHeight()/2;
//        for(int i = 0;i<iu.getWidth();i++){
//            if(iu.getColor(r,i)[0]!=(iu.getColor(1,1)[0])){
//                for (double d:iu.getColor(r,i)
//                ) {
//                    System.out.print(d + " ");
//                }
//                System.out.println("");
//            }
//        }
    }
}
