package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PointData {
    private int point_id;
    private int point_num;
    private int voice_num;
    private int point_type;
    private int point_x;
    private int point_y;
    private String material;
    private String direction;
    private String defect;
    private String architecture;
    private String length_unit;
    private double length;
    private String width_unit;
    private double width;
    private String height_unit;
    private double height;
    private int count;
    private List<PointImg> point_img;
    private List<PointVoice> point_voice;
    private List<PointMemo> point_memo;
    private DrawingPointData drawingPointData;

    public int getPoint_id() {
        return point_id;
    }
    public int getPoint_num() {
        return point_num;
    }
    public int getVoice_num() {
        return voice_num;
    }
    public void setVoice_num(int voice_num) {
        this.voice_num = voice_num;
    }
    public int getPoint_type() {
        return point_type;
    }
    public int getPoint_x() {
        return point_x;
    }
    public int getPoint_y() {
        return point_y;
    }
    public String getMaterial() {
        return material;
    }
    public String getDirection() {
        return direction;
    }
    public String getDefect() {
        return defect;
    }
    public String getArchitecture() {
        return architecture;
    }
    public String getLength_unit() {
        return length_unit;
    }
    public double getLength() {
        return length;
    }
    public String getWidth_unit() {
        return width_unit;
    }
    public double getWidth() {
        return width;
    }
    public String getHeight_unit() {
        return height_unit;
    }
    public double getHeight() {
        return height;
    }
    public int getCount() {
        return count;
    }
    public List<PointImg> getPoint_img() {
        return point_img;
    }
    public List<PointVoice> getPoint_voice() {
        return point_voice;
    }
    public List<PointMemo> getPoint_memo() {
        return point_memo;
    }
    public DrawingPointData getDrawingPointData() {
        return drawingPointData;
    }
    public void setDrawingPointData() {
        drawingPointData = new DrawingPointData(new PointF(point_x, point_y), DrawingPointData.DrawingType.values()[point_type - 1]);
        if (point_img != null && !point_img.isEmpty()) {
            List<Bitmap> regImageList = new ArrayList<>();
            boolean isFirst = true;
            for (PointImg img : point_img) {
                if (isFirst) {
                    drawingPointData.setRegImage(img.getImgBitmap());
                    isFirst = false;
                }
                regImageList.add(img.getImgBitmap());
            }
            drawingPointData.setRegImageList(regImageList);
        }
    }

    public static class PointImg {
        private int img_id;
        private String img_url;
        private Bitmap imgBitmap;
        private Uri imgUri;

        public PointImg(int img_id, String img_url) {
            this.img_id = img_id;
            this.img_url = img_url;
        }

        public int getImg_id() {
            return img_id;
        }
        public String getImg_url() {
            return img_url;
        }
        public Bitmap getImgBitmap() {
            return imgBitmap;
        }
        public void setImgBitmap(Bitmap imgBitmap) {
            this.imgBitmap = imgBitmap;
        }
        public Uri getImgUri() {
            return imgUri;
        }
        public void setImgUri(Uri imgUri) {
            this.imgUri = imgUri;
        }
    }

    public static class PointVoice {
        private int voice_id;
        private int voice_num;
        private int voice_time;
        private String voice_file;
        private File voiceFile;
        private String voiceName;
        private int playTime;
        private boolean isPlay;

        public PointVoice(int voice_id, int voice_num, int voice_time, File voiceFile, String voiceName) {
            this.voice_id = voice_id;
            this.voice_num = voice_num;
            this.voice_time = voice_time;
            this.voiceFile = voiceFile;
            this.voiceName = voiceName;
            this.playTime = 0;
            this.isPlay = false;
        }

        public int getVoice_id() {
            return voice_id;
        }
        public int getVoice_num() {
            return voice_num;
        }
        public int getVoice_time() {
            return voice_time;
        }
        public String getVoice_file() {
            return voice_file;
        }
        public File getVoiceFile() {
            return voiceFile;
        }
        public void setVoiceFile(File voiceFile) {
            this.voiceFile = voiceFile;
        }
        public String getVoiceName() {
            return voiceName;
        }
        public void setVoiceName(String voiceName) {
            this.voiceName = voiceName;
        }
        public int getPlayTime() {
            return playTime;
        }
        public void setPlayTime(int playTime) {
            if (playTime < 0) {
                this.playTime = 0;
            } else if (playTime > voice_time) {
                this.playTime = voice_time;
            } else {
                this.playTime = playTime;
            }
        }
        public boolean isPlay() {
            return isPlay;
        }
        public void setPlay(boolean play) {
            isPlay = play;
        }
    }

    public static class PointMemo {
        private int memo_id;
        private String memo_url;
        private String memoBitmapName;
        private Bitmap memoBitmap;

        public PointMemo(int memo_id, String memo_url) {
            this.memo_id = memo_id;
            this.memo_url = memo_url;
        }

        public int getMemo_id() {
            return memo_id;
        }
        public String getMemo_url() {
            return memo_url;
        }
        public String getMemoBitmapName() {
            return memoBitmapName;
        }
        public void setMemoBitmapName(String memoBitmapName) {
            this.memoBitmapName = memoBitmapName;
        }
        public Bitmap getMemoBitmap() {
            return memoBitmap;
        }
        public void setMemoBitmap(Bitmap memoBitmap) {
            this.memoBitmap = memoBitmap;
        }
    }
}
