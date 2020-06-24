package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class PointData {
    private int point_id;
    private int point_type;
    private int point_x;
    private int point_y;
    private String material;
    private String direction;
    private String defect;
    private String architecture;
    private String length_unit;
    private int length;
    private String width_unit;
    private int width;
    private String height_unit;
    private int height;
    private int count;
    private List<PointImg> point_img;
    private List<PointVoice> point_voice;
    private List<PointMemo> point_memo;
    private DrawingPointData drawingPointData;

    public int getPoint_id() {
        return point_id;
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
    public int getLength() {
        return length;
    }
    public String getWidth_unit() {
        return width_unit;
    }
    public int getWidth() {
        return width;
    }
    public String getHeight_unit() {
        return height_unit;
    }
    public int getHeight() {
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
                }
                regImageList.add(img.getImgBitmap());
            }
            drawingPointData.setRegImageList(regImageList);
        }
    }

    public class PointImg {
        private int img_id;
        private String img_url;
        private Bitmap imgBitmap;

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
    }

    public class PointVoice {
        private int voice_id;
        private String voice_title;
        private int voice_time;
        private String voice_file;

        public PointVoice(int voice_id, String voice_title, int voice_time, String voice_file) {
            this.voice_id = voice_id;
            this.voice_title = voice_title;
            this.voice_time = voice_time;
            this.voice_file = voice_file;
        }

        public int getVoice_id() {
            return voice_id;
        }
        public void setVoice_id(int voice_id) {
            this.voice_id = voice_id;
        }
        public String getVoice_title() {
            return voice_title;
        }
        public void setVoice_title(String voice_title) {
            this.voice_title = voice_title;
        }
        public int getVoice_time() {
            return voice_time;
        }
        public void setVoice_time(int voice_time) {
            this.voice_time = voice_time;
        }
        public String getVoice_file() {
            return voice_file;
        }
        public void setVoice_file(String voice_file) {
            this.voice_file = voice_file;
        }
    }

    public class PointMemo {
        private int memo_id;
        private String memo_url;

        public PointMemo(int memo_id, String memo_url) {
            this.memo_id = memo_id;
            this.memo_url = memo_url;
        }

        public int getMemo_id() {
            return memo_id;
        }
        public void setMemo_id(int memo_id) {
            this.memo_id = memo_id;
        }
        public String getMemo_url() {
            return memo_url;
        }
        public void setMemo_url(String memo_url) {
            this.memo_url = memo_url;
        }
    }
}
