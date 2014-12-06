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
	int v = 0; /*Value to be written to each channel*/

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