int3 get_rgb(int pix)
{
	int3 rgb = (int3)(0);
	int red = (pix >> 16) & 0xFF;
	int green = (pix >> 8) & 0xFF;
	int blue = (pix) & 0xFF;

	rgb.x = red;
	rgb.y = green;
	rgb.z = blue;
	return rgb;
}

__kernel void invert(__global const int *img_buff, __global int *result)
{
	unsigned int id = get_global_id(0);
	
	int3 rgb = get_rgb(img_buff[id]);
	
	int red = 255 - rgb.x;
	int blue = 255 - rgb.y;
	int green = 255 - rgb.z;

	int pix = (0xFF << 24) + (red << 16) + (green << 8) + blue;

	result[id] = pix;
}

__kernel void gray_scale(__global const int *img_buff, __global int *result)
{
	unsigned int id = get_global_id(0);

	int3 rgb = get_rgb(img_buff[id]);
	
	uint red = rgb.x;
	uint blue = rgb.y;
	uint green = rgb.z;

	int avg = (red + green + blue) / 3;

	int pix = (0xFF << 24) + (avg << 16) + (avg << 8) + avg;

	result[id] = pix;
}

__kernel void emboss(__global const int *img_buff, __global int *result, const int width)
{
	unsigned int id = get_global_id(0);
	
	int3 curPix = get_rgb(img_buff[id]);
	int curRed = curPix.x;
	int curGreen = curPix.y;
	int curBlue = curPix.z;

	int redDiff = 0;
	int greenDiff = 0;
	int blueDiff = 0;
	int v = 0;

	if(id < width || id % width == 0)
	    v = 128;
	else
	{
		int3 topLeftPix = get_rgb(img_buff[id - width - 1]);
		int tlRed = topLeftPix.x;
		int tlGreen = topLeftPix.y;
		int tlBlue = topLeftPix.z;

		redDiff = curRed - tlRed;
		greenDiff = curGreen - tlGreen;
		blueDiff = curBlue - tlBlue;

		int maxDiff = 0;
	    if(abs(redDiff) >= abs(greenDiff))
	        maxDiff = redDiff;
	    else if(abs(greenDiff) >= abs(blueDiff))
	    	maxDiff = greenDiff;
	    else
	    	maxDiff = blueDiff;
	    
	    v = 128 + maxDiff;
        if(v < 0)
        	v = 0;
        if(v > 255)
            v = 255;
	}

	int pix = (0xFF << 24) + (v << 16) + (v << 8) + v;
	result[id] = pix;
}

__kernel void blur(__global const int *img_buff, __global int *result, const int width)
{
	unsigned int id = get_global_id(0);
	int blur = 200;
	int x = id % width;
	int y = id / width;

	int max = 0;
	if(x + blur < width)
		max = x + blur;
	else
	{
		max = width;
		blur = width - x;
	}

	int redSum = 0;
	int greenSum = 0;
	int blueSum = 0;

	int i;
	for(i = x; i < max; i++)
	{
		int3 curPix = get_rgb(img_buff[y * width + i]);
		redSum += curPix.x;
		greenSum += curPix.y;
		blueSum += curPix.z;
	}

	int red = redSum / blur;
	int green = greenSum / blur;
	int blue = blueSum / blur;

	int pix = (0xFF << 24) + (red << 16) + (green << 8) + blue;
	result[id] = pix;
}