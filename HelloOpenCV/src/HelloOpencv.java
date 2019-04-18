import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * class name: OpenCV
 */
public class HelloOpencv {
    private final double[] WHITE = {255.0, 255.0, 255.0};
    private final double[] BLACK = {0.0, 0.0, 0.0};

    public ImageUtils binarize(ImageUtils iu) {
        Mat image = iu.getMat();
        ImageUtils bi = new ImageUtils();
        bi.setMat(new Mat());
        image.copyTo(bi.getMat());
        Imgproc.cvtColor(image, bi.getMat(), Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(bi.getMat(), bi.getMat(), 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        return bi;
    }

    public Rect cutImg(ImageUtils iu) {
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
        Rect rect = new Rect(0, upbound, iu.getWidth(), lowerbound - upbound);

        return rect;
    }

    public Rect cutImg2(ImageUtils iu) {
        //up bound:
        boolean allBlack = true;
        int i;
        for (i = 0; i < iu.getHeight(); i++) {
            for (int j = 0; j < iu.getWidth(); j++) {
                if (iu.getPixel(i, j) == WHITE[0]) {
                    allBlack = false;
                    break;
                }
            }
            if (!allBlack) break;
        }

        //low bound:
        allBlack = true;
        int l;
        for (l = iu.getHeight() - 1; l >= 0; l--) {
            for (int j = 0; j < iu.getWidth(); j++) {
                if (iu.getPixel(l, j) == WHITE[0]) {
                    allBlack = false;
                    break;
                }
            }
            if (!allBlack) break;
        }

        //left bound:
        allBlack = true;
        int left;
        for (left = 0; left < iu.getWidth(); left++) {
            for (int j = 0; j < iu.getHeight(); j++) {
                if (iu.getPixel(j, left) == WHITE[0]) {
                    allBlack = false;
                    break;
                }
            }
            if (!allBlack) break;
        }

        //right bound:
        allBlack = true;
        int right;
        for (right = iu.getWidth() - 1; right >= 0; right--) {
            for (int j = 0; j < iu.getHeight(); j++) {
                if (iu.getPixel(j, right) == WHITE[0]) {
                    allBlack = false;
                    break;
                }
            }
            if (!allBlack) break;
        }

        Rect rect = new Rect(left, i, right - left, l - i);
        return rect;
    }

    public int getRowNum(ImageUtils iu) {
        boolean allBlack;
        boolean flag = true;
        int count = 0;
        for (int i = 0; i < iu.getHeight(); i++) {
            allBlack=true;
            for (int j = 0; j < iu.getWidth(); j++) {
                if (iu.getPixel(i, j) == WHITE[0]) {
                    allBlack = false;
                    flag = true;
                    break;
                }
            }
            if (allBlack && flag) {
                count++;
                flag = false;
            }
        }
        return count+1;
    }

    public int getColNum(ImageUtils iu) {
        boolean allBlack;
        boolean flag = true;
        int count = 0;
        for (int i = 0; i < iu.getWidth(); i++) {
            allBlack=true;
            for (int j = 0; j < iu.getHeight(); j++) {
                if (iu.getPixel(j,i) == WHITE[0]) {
                    allBlack = false;
                    flag = true;
                    break;
                }
            }
            if (allBlack && flag) {
                count++;
                flag = false;
            }
        }
        return count+1;
    }

//    public int getStartPoint(ImageUtils iu){
//
//    }

    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        HelloOpencv h = new HelloOpencv();
        ImageUtils iu = new ImageUtils("images\\origin.jpg");
        ImageUtils bi = h.binarize(iu);
        bi.writeImg("images\\binarized.jpg");
        //colored:
        Rect r1 = h.cutImg(bi);
        ImageUtils precut = new ImageUtils();
        precut.setMat(new Mat(iu.getMat(), r1));
        //binarized version:
        ImageUtils cut1 = new ImageUtils();
        cut1.setMat(new Mat(bi.getMat(), r1));
        //colored 2:
        Rect r2 = h.cutImg2(cut1);
        precut.setMat(new Mat(precut.getMat(), r2));
        //binarized 2:
        ImageUtils cut2 = new ImageUtils();
        cut2.setMat(new Mat(cut1.getMat(), r2));
        cut2.writeImg("images\\cut2.jpg");

        //write them into jpg file
        precut.writeImg("images\\cut().jpg");
    }
}
