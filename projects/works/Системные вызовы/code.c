#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/moduleparam.h>
#include <linux/unistd.h>
#include <asm/cacheflush.h>
#include <linux/kallsyms.h>
#include <linux/utsname.h>
#define GPF_DISABLE write_cr0(read_cr0() & (~ 0x10000))
#define GPF_ENABLE write_cr0(read_cr0() | 0x10000)
// sys_call_table address in System.map
void **sys_call_table = (void*)0xffffffff818001c0;
long unsigned int addr = 0xffffffff818001c0;
asmlinkage int (*original_call) (const char*, int, int);
asmlinkage int (*original_syslog) (int, int, int);
asmlinkage int (*original_ptrace) (int, int);
asmlinkage int (*original_uname) (struct old_utsname __user *);
asmlinkage int (*original_sysinfo) (int, int);
asmlinkage long my_syslog(int which, int who, int niceval)
{
printk("System call syslog was catched: which = %d, who = %d, niceval = %d\n", which, who, niceval);
return original_syslog(which, who, niceval);
}	
asmlinkage long my_ptrace(int which, int who)
{
printk("System call ptrace was catched: which = %d, who = %d\n", which, who);
return original_ptrace(which, who);
}
asmlinkage int my_uname(struct old_utsname __user *name)
{
printk("System call uname was catched!\n");
return original_uname(name);
}
asmlinkage long my_sysinfo(int which, int who)
{
printk("System call sysinfo was catched: which = %d, who = %d\n", which, who);
return original_sysinfo(which, who);
}

int init_module()
{
original_syslog = sys_call_table[__NR_syslog];
original_ptrace = sys_call_table[__NR_ptrace];
original_uname = sys_call_table[__NR_uname];
original_ sysinfo = sys_call_table[__NR_sysinfo];
GPF_DISABLE;
sys_call_table[__NR_syslog] = my_syslog;
sys_call_table[__NR_ptrace] = my_ptrace;
sys_call_table[__NR_uname] = my_uname;
sys_call_table[__NR_sysinfo] = my_sysinfo;
GPF_ENABLE;
return 0;
}
void cleanup_module()
{
// Restore the original calls
GPF_DISABLE;
sys_call_table[__NR_ syslog] = original_syslog;
sys_call_table[__NR_ ptrace] = original_ptrace;
sys_call_table[__NR_uname] = original_uname;
sys_call_table[__NR_sysinfo] = original_sysinfo;
