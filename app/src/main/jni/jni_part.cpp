#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace std;

/** by rootming
* JNI调用
*/


void imagefix(char* orgfile, char* maskfile, char* outfile);
void removemovingobjects(int len,char* paths,char* savepath,int photonum,float prevX, float prevY, float curX, float curY);
void videostable(int len, char* paths);

char * Jstring2CStr( JNIEnv * env, jstring jstr ) 
{ 
    char * rtn = NULL; 
    jclass clsstring = env->FindClass("java/lang/String"); 
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B"); 
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr,mid,strencode); 
    jsize alen = env->GetArrayLength(barr); 
    jbyte * ba = env->GetByteArrayElements(barr,JNI_FALSE); 
    if(alen > 0) 
    { 
        rtn = (char*)malloc(alen+1); //new char[alen+1]; 
        memcpy(rtn,ba,alen); 
        rtn[alen]=0; 
    } 
    env->ReleaseByteArrayElements(barr,ba,0); 

    return rtn;
}

extern "C" {
JNIEXPORT void JNICALL Java_com_rootming_inpaintcamera_RectActivity_ProcImg(JNIEnv* env, jobject thiz,jint len,jstring paths,jstring savepath,jint photonum,jfloat prevX,jfloat prevY,jfloat curX,jfloat curY)
{
    removemovingobjects(len,Jstring2CStr(env,paths),Jstring2CStr(env,savepath),photonum,prevX,prevY,curX,curY);
}

JNIEXPORT void JNICALL Java_com_rootming_inpaintcamera_FullscreenActivity_StabImg(JNIEnv* env, jobject thiz,jint len,jstring paths)
{
    videostable(len,Jstring2CStr(env,paths));
}

JNIEXPORT void JNICALL Java_com_rootming_inpaintcamera_FixActivity_FixImg(JNIEnv* env, jobject thiz,jstring orgfile,jstring maskfile,jstring outfile)
{
    imagefix(Jstring2CStr(env,orgfile),Jstring2CStr(env,maskfile),Jstring2CStr(env,outfile));
}
}
