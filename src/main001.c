#include <stdio.h>
#include <time.h>
#include <sys/time.h>
#include <errno.h>

int main(int argc, char* argv[]){
	struct timeval tv;
	struct tm *tm;
	/* */
	if ( gettimeofday(&tv,NULL) != 0 ) {
		fprintf(stderr,"%s\n",strerror(errno));
		return -1;
	}
	/* */
	if ( ( tm = localtime(&tv.tv_sec) ) == NULL ) {
		return -1;
	}
	/* */
	fprintf(stdout,"%04d/%02d/%02d %02d:%02d:%02d.%06d\n"
		,tm->tm_year + 1900
		,tm->tm_mon + 1
		,tm->tm_mday
		,tm->tm_hour
		,tm->tm_min
		,tm->tm_sec
		,tv.tv_usec);
	return 0;
}
