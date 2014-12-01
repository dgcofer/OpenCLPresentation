__kernel void invert(__global const int *buff, __global int *ans)
{
	unsigned int id = get_global_id(0);
	
	int3 rgb = get_rgb(buff[id]);
	
	int red = 255 - rgb.x;
	int blue = 255 - rgb.y;
	int green = 255 - rgb.z;

	int pix = (0xFF << 24) + (red << 16) + (green << 8) + blue;

	ans[id] = pix;
}