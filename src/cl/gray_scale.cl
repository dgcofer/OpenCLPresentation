__kernel void gray_scale(__global int *buff)
{
	unsigned int id = get_global_id(0);

	unsigned int red = (buff[id] >> 16) & 0xFF;
	unsigned int green = (buff[id] >> 8) & 0xFF;
	unsigned int blue = (buff[id]) & 0xFF;

	int avg = (red + green + blue) / 3;

	int pix = (0xFF << 24) + (avg << 16) + (avg << 8) + avg;

	buff[id] = pix;
}