/* config.h */
#ifndef	CONFIG_H
#define CONFIG_H


#define DEBUG

#include <cutils/log.h>

#ifdef DEBUG
#define debug ALOGE
#else
#define debug 
#endif

#endif /* CONFIG_H */

