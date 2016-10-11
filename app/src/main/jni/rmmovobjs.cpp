#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

/** by rootming
* 排序像素, 并返回中间像素
*/
int calmeans(int len,int* arr)
{
	for(int i = 1;i < len;i++)
	{
		for(int j = i;j >0;j--)
		{
			if(arr[j] < arr[j-1])
			{
				int temp = arr[j];
				arr[j] = arr[j-1];
				arr[j-1] = temp;
			}
			else
			{
				continue;
			}
		}
	}
	return arr[len/2];
}

/** by rootming
* 删除移动的干扰物体
*/

void removemovingobjects(int len, char* paths,char* savepath,int photonum,float prevX, float prevY, float curX, float curY){
//	cv::Mat imgs[len];
	cv::Mat *imgs = new cv::Mat[len];
	int i = 0;
	const char *delim = "*";
	char *tokenPtr = strtok(paths,delim);
	while(tokenPtr != NULL)
	{
		imgs[i] = cv::imread(tokenPtr);
		tokenPtr=strtok(NULL,delim);
		i++;
	}
	
	cv::Mat result(imgs[0]);

	for(int row = 0;row < imgs[0].rows;row++)
	{
		for(int col = 0;col < imgs[0].cols;col++)
		{
			if(row >= prevY && row <= curY && col >= prevX && col <= curX){
				result.at<cv::Vec3b>(row,col) = imgs[photonum-1].at<cv::Vec3b>(row,col); //选框内直接复制像素
			}
			else{
				cv::Vec3b pixel;
				int arr1[len];
				int arr2[len];
				int arr3[len];
				for (int j = 0; j < len; ++j)
				{
					pixel = imgs[j].at<cv::Vec3b>(row,col);
					arr1[j] = (int)pixel[0];
					arr2[j] = (int)pixel[1];
					arr3[j] = (int)pixel[2];
				}

				int channel1 = calmeans(len,arr1);
				int channel2 = calmeans(len,arr2);
				int channel3 = calmeans(len,arr3);

				result.at<cv::Vec3b>(row,col) = cv::Vec3b(channel1,channel2,channel3);
			}
		}
	}
	
	vector<int> compression_params;
	compression_params.push_back(CV_IMWRITE_JPEG_QUALITY);
	compression_params.push_back(100);
	cv::imwrite(savepath,result,compression_params);

	for (int j = 0; j < len; ++j)
	{
		imgs[j].release();
	}

	free(paths);
	free(savepath);
}