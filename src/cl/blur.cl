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