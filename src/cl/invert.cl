__kernel void invert(__global int *buff)
{
	unsigned int id = get_global_id(0);
	
	unsigned int red = (buff[id] >> 16) & 0xFF;
	unsigned int green = (buff[id] >> 8) & 0xFF;
	unsigned int blue = (buff[id]) & 0xFF;
	red = 255 - red;
	blue = 255 - blue;
	green = 255 - green;

	int pix = (0xFF << 24) + (red << 16) + (green << 8) + blue;

	buff[id] = pix;
}