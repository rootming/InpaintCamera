#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/video/video.hpp>
#include <iostream>


#include <android/log.h>
#define LOG_TAG "yanzi"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace cv;
using namespace std;

/** by rootming
* 消除抖动
*/

class Tracker {
    vector<Point2f> trackedFeatures;
    Mat             prevGray;
public:
    bool            freshStart;
    Mat_<float>     rigidTransform;

    Tracker():freshStart(true) {
        rigidTransform = Mat::eye(3,3,CV_32FC1); //affine 2x3 in a 3x3 matrix
    }

    void processImage(Mat& img) {
        Mat gray;
        cvtColor(img,gray,CV_BGR2GRAY);
        vector<Point2f> corners;
        if(trackedFeatures.size() < 200) {
            goodFeaturesToTrack(gray,corners,300,0.01,10);
            cout << "found " << corners.size() << " features\n";
            for (int i = 0; i < corners.size(); ++i) {
                trackedFeatures.push_back(corners[i]);
            }
        }

        if(!prevGray.empty()) {
            vector<uchar> status; vector<float> errors;
            calcOpticalFlowPyrLK(prevGray,gray,trackedFeatures,corners,status,errors,Size(10,10));

            if(countNonZero(status) < status.size() * 0.8) {
                cout << "cataclysmic error \n";
                rigidTransform = Mat::eye(3,3,CV_32FC1);
                trackedFeatures.clear();
                prevGray.release();
                freshStart = true;
                return;
            } else
                freshStart = false;

            Mat_<float> newRigidTransform = estimateRigidTransform(trackedFeatures,corners,false);
            Mat_<float> nrt33 = Mat_<float>::eye(3,3);
            newRigidTransform.copyTo(nrt33.rowRange(0,2));
            rigidTransform *= nrt33;

            trackedFeatures.clear();
            for (int i = 0; i < status.size(); ++i) {
                if(status[i]) {
                    trackedFeatures.push_back(corners[i]);
                }
            }
        }

        //for (int i = 0; i < trackedFeatures.size(); ++i) {
        //    circle(img,trackedFeatures[i],3,Scalar(0,0,255),CV_FILLED);
        //}

        gray.copyTo(prevGray);
    }
};


void videostable(int len, char* paths) {
    LOGI("%s", paths);
    vector<int> compression_params;
    compression_params.push_back(CV_IMWRITE_JPEG_QUALITY);
    compression_params.push_back(100);

    Mat frame,orig,orig_warped;
    Tracker tracker;
    const char *delim = "*";
    char *tokenPtr = strtok(paths,delim);
    while(tokenPtr != NULL)
    {
        frame = cv::imread(tokenPtr);
        //if(frame.empty()) break;
        frame.copyTo(orig);

        tracker.processImage(orig);

        Mat invTrans = tracker.rigidTransform.inv(DECOMP_SVD);
        warpAffine(frame,orig_warped,invTrans.rowRange(0,2),Size());

        cv::imwrite(tokenPtr,orig_warped,compression_params);
        frame.release();
        orig.release();
        orig_warped.release();

        tokenPtr=strtok(NULL,delim);
    }

    free(paths);
}