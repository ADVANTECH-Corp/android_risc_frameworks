#include"advdaemon.h"
#include "config.h"
#include "types.h"
#define VNCSERVER_EX "androidvncserver"

static void killAllVnc()
{
    FILE *fp = NULL;
    fp = popen("ps | grep 'androidvncserver' | cut -c 9-15 | xargs kill -9", "r");
    pclose(fp);
}

int _startvnc(char *passwd, char reply[REPLY_MAX]){
	int status = -1;
    char cmdline[1024];
	sprintf(cmdline, "%s -p %s &", VNCSERVER_EX, passwd);
	status = system(cmdline);
    if(status < 0)
    {
        debug("startvnc failed : %s\n", strerror(errno));
        return RET_FAIL;
    }
    else
    {

        if (WIFEXITED(status))
        {
            if (0 == WEXITSTATUS(status))
            {
                debug("startvnc successfully\n");
                return RET_OK;
            }
            else
            {
                debug("startvnc failed, script exit code:  %d\n", WEXITSTATUS(status));
                return RET_FAIL;
            }
        }
        else
        {
            debug("startvnc failed,  exit code:  %d\n", WEXITSTATUS(status));
            return RET_FAIL;
        }
    }
}

int _stopvnc(char reply[REPLY_MAX]){
	char *p = "stopvnc ok";
	killAllVnc();
	memcpy(reply, p, strlen(p));
	return RET_OK;
}
