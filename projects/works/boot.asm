%include "io.inc" 

section .text 
global CMAIN 
CMAIN: 
BITS 16 
org0x700 
; ������� ��������, ��������� ���� 
cli 
mov ax, 0 
mov ds, ax 
mov es, ax 
mov ss, ax 
mov sp, 0x700 
sti 
;��������� � ����������� 
mov si, msg_start 
call kputs 
; ��������� � �������� � ���������� ����� 
mov si, msg_entering_pmode 
call ps 
; ���������� ������� (������ ���) 
mov ah, 1 
mov ch, 0x20 
int 0x10 
; ��������� ������� ������ ����������� ���������� � 0x20 
mov al,00010001b 
out 0x20,al 
mov al,0x20 
out 0x21,al 
mov al,00000100b 
out 0x21,al 
mov al,00000001b 
out 0x21,al 
; �������� ���������� 
cli 
; �������� �������� GDTR: 
lgdt [gd_reg] 
; ��������� A20: 
in al, 0x92 
or al, 2 
out 0x92, al 
; ��������� ���� PE �������� CR0 
mov eax, cr0 
or al, 1 
mov cr0, eax 
; � ������� �������� ������ �� ��������� �������� ������� �������� � ������� CS 
jmp 0x8: _protect 
ps: 
pusha 
.loop: 
lodsb 
test al, al 
jz .quit 
mov ah, 0x0e 
int 0x10 
jmp short .loop 
.quit: 
popa 
ret 
; ��������� ��� � 32-������ 
[BITS 32] 
;��� �������� � ���������� �����, ���� ����� ������ ���������� 
_protect: 
; �������� �������� DS � SS ���������� �������� ������ 
mov ax, 0x10 
mov ds, ax 
mov es, ax 
mov ss, ax 
; ���� ���� ���������� �� ������ 2��, ��������� ��� ����. ker_bin � �����, ����� ������� ��������� ���� 
mov esi, ker_bin 
; �����, �� �������� �������� 
mov edi, 0x200000 
; ������ ���� � ������� ������ (65536 ����) 
mov ecx, 0x4000 
rep movsd 
; ���� �����������, �������� ���������� ��� 
jmp 0x200000 
gdt: 
dw 0, 0, 0, 0 
; ������� ���������� 
db 0xFF 
; ������� ���� � DPL=0 �����=0 � �������=4 �� 
db 0xFF 
db 0x00 
db 0x00 
db 0x00 
db 10011010b 
db 0xCF 
db 0x00 
db 0xFF 
; ������� ������ � DPL=0 �����=0 � �������=4�� 
db 0xFF 
db 0x00 
db 0x00 
db 0x00 
db 10010010b 
db 0xCF 
db 0x00 
; ��������, ������� �� �������� � GDTR: 
gd_reg: 
dw 8192 
dd gdt 
msg_start: db �Get fun! New loader is on�, 0x0A, 0x0D, 0 
msg_epm: db �Protected mode is greeting you�, 0x0A, 0x0D, 0 
xor eax, eax 
ret