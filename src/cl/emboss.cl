uint3 get_rgb(int pix)
{
	uint3 rgb = (uint3)(0);
	uint red = (pix >> 16) & 0xFF;
	uint green = (pix >> 8) & 0xFF;
	uint blue = (pix) & 0xFF;

	rgb.x = red;
	rgb.y = green;
	rgb.z = blue;
	return rgb;
}

__kernel void emboss(__global int *buff, const int width)
{
	unsigned int id = get_global_id(0);
	
	uint3 curPix = get_rgb(buff[id]);

	int redDiff = 0;
	int greenDiff = 0;
	int blueDiff = 0;
	int v = 0;

	if(id < width || id % width == 0)
	{
	    v = 128;
	}
	else
	{
		uint3 topLeftPix = get_rgb(buff[id - width - 1]);

		redDiff = curPix.x - topLeftPix.x;
		greenDiff = curPix.y - topLeftPix.y;
		blueDiff = curPix.z - topLeftPix.z;

		int maxDiff = 0;
	    if(abs(redDiff) >= abs(greenDiff))
	    {
	        maxDiff = redDiff;
	    }
	    else if(abs(greenDiff) >= abs(blueDiff))
	    {
	    	maxDiff = greenDiff;
	    }
	    else
	    {
	    	maxDiff = blueDiff;
	    }

	    v = 128 + maxDiff;
        if(v < 0)
        	v = 0;
        if(v > 255)
            v = 255;
	}

	int pix = (0xFF << 24) + (v << 16) + (v << 8) + v;
	buff[id] = pix;
}