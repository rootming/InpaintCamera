/*M///////////////////////////////////////////////////////////////////////////////////////
//
//  IMPORTANT: READ BEFORE DOWNLOADING, COPYING, INSTALLING OR USING.
//
//  By downloading, copying, installing or using the software you agree to this license.
//  If you do not agree to this license, do not download, install,
//  copy or use the software.
//
//
//                           License Agreement
//                For Open Source Computer Vision Library
//
// Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
// Copyright (C) 2008-2012, Willow Garage Inc., all rights reserved.
// Third party copyrights are property of their respective owners.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
//   * Redistribution's of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//
//   * Redistribution's in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//
//   * The name of the copyright holders may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// This software is provided by the copyright holders and contributors "as is" and
// any express or implied warranties, including, but not limited to, the implied
// warranties of merchantability and fitness for a particular purpose are disclaimed.
// In no event shall the Intel Corporation or contributors be liable for any direct,
// indirect, incidental, special, exemplary, or consequential damages
// (including, but not limited to, procurement of substitute goods or services;
// loss of use, data, or profits; or business interruption) however caused
// and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of
// the use of this software, even if advised of the possibility of such damage.
//
//M*/

#include "inpainter.h"

using namespace cv;


static cv::Mat imageScale(char* file, double scale, int flags)
{
    Mat image = imread(file, flags);
    Size dsize = Size(image.cols * scale, image.rows * scale);
    Mat image2 = Mat(dsize, CV_32S);
    cv::resize(image, image2, dsize);
    return image2;
}


void imagefix(char* orgfile, char* maskfile, char* outfile)
{
    cv::Mat image, originalImage, inpaintMask;
    double scale = 0.2;
    int halfPatchWidth = 8;
    char* imageName = orgfile;
    char* maskName = maskfile;

    //originalImage=cv::imread(imageName,CV_LOAD_IMAGE_COLOR);
    originalImage = imageScale(imageName, scale, CV_LOAD_IMAGE_COLOR);

    if(!originalImage.data){
        std::cout<<std::endl<<"Error unable to open input image"<<std::endl;
    }

    image = originalImage.clone();

    //inpaintMask = cv::imread(maskName,CV_LOAD_IMAGE_GRAYSCALE);
    inpaintMask = imageScale(maskName, scale, CV_LOAD_IMAGE_GRAYSCALE);
    Inpainter i(originalImage, inpaintMask, halfPatchWidth);
    if(i.checkValidInputs() == i.CHECK_VALID){
        i.inpaint();
        cv::imwrite(outfile, i.result);
    }else{
        std::cout<<std::endl<<"Error : invalid parameters"<<std::endl;
    }

}
