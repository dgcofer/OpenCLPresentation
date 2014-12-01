__kernel void gray_scale(__global const int *buff, __global int *ans)
{
	unsigned int id = get_global_id(0);

	int3 rgb = get_rgb(buff[id]);
	
	uint red = rgb.x;
	uint blue = rgb.y;
	uint green = rgb.z;

	int avg = (red + green + blue) / 3;

	int pix = (0xFF << 24) + (avg << 16) + (avg << 8) + avg;

	ans[id] = pix;
}