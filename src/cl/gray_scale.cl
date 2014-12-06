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