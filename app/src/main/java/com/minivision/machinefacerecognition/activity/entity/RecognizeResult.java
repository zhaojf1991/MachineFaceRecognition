package com.minivision.machinefacerecognition.activity.entity;

import java.util.List;

/**
 * Created by yuan_hao on 2017/4/12.
 * Email:yuanhao@minivision.cn
 */
public class RecognizeResult{

    /**
     * age : -4
     * distance : 0.0
     * errorCode : -2
     * faceRect_h : 0
     * faceRect_w : 0
     * faceRect_x : 0
     * faceRect_y : 0
     * imagePath :
     * name :
     * score : 0.0
     * sex : -4
     */

    private List<RecognizeResultsBean> RecognizeResults;

    public List<RecognizeResultsBean> getRecognizeResults() {
        return RecognizeResults;
    }

    public void setRecognizeResults(List<RecognizeResultsBean> RecognizeResults) {
        this.RecognizeResults = RecognizeResults;
    }

    public static class RecognizeResultsBean {
        private int age;
        private double distance;
        private int errorCode;
        private float faceRect_h;
        private float faceRect_w;
        private float faceRect_x;
        private float faceRect_y;
        private String imagePath;
        private String name;
        private double score;
        private int sex;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public float getFaceRect_h() {
            return faceRect_h;
        }

        public void setFaceRect_h(int faceRect_h) {
            this.faceRect_h = faceRect_h;
        }

        public float getFaceRect_w() {
            return faceRect_w;
        }

        public void setFaceRect_w(int faceRect_w) {
            this.faceRect_w = faceRect_w;
        }

        public float getFaceRect_x() {
            return faceRect_x;
        }

        public void setFaceRect_x(int faceRect_x) {
            this.faceRect_x = faceRect_x;
        }

        public float getFaceRect_y() {
            return faceRect_y;
        }

        public void setFaceRect_y(int faceRect_y) {
            this.faceRect_y = faceRect_y;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        @Override
        public String toString() {
            return "{" +
                    "errorCode=" + errorCode +
                    ", faceRect_h=" + faceRect_h +
                    ", faceRect_w=" + faceRect_w +
                    ", faceRect_y=" + faceRect_y +
                    ", faceRect_x=" + faceRect_x +
                    ", imagePath='" + imagePath + '\'' +
                    ", name='" + name + '\'' +
                    ", score=" + score +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof RecognizeResultsBean)) {
                return false;
            }

            RecognizeResultsBean bean = (RecognizeResultsBean) o;

            return this == o || this.getName().equals(bean.getName());
        }
    }

}
