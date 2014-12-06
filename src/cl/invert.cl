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