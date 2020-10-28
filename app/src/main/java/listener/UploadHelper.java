package listener;

/**
 * Created by fan.zhengxi on 2020/10/28
 * Describe:
 */
public class UploadHelper {
    public static RecorderFileListener recorderFileListener;

    public static RecorderFileListener getRecorderFileListener() {
        return recorderFileListener;
    }

    public static void setRecorderFileListener(RecorderFileListener recorderFileListener) {
        UploadHelper.recorderFileListener = recorderFileListener;
    }
}
