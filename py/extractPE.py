﻿#coding:utf-8
import PeStructIml
import os
import pefile
from operator import itemgetter
from macpath import join
import traceback

class extractPE(PeStructIml):
    def __init__(self):
        self.pe = ""
        self.bin = None
        self.pcount = 0
        self.res = ""

    def writeNum(self, s):
        ++self.pcount
        self.res += str(s) + ","

    def saveDIRECTORY_ENTRY_IMPORT(self):
        dlls = []
        apis = []
        testdlls = ['MSCOREE.DLL', 'MSVBVM60.DLL', 'SHLWAPI.DLL', 'NTDLL.DLL', 'WSOCK32.DLL', 'MSVCR90.DLL',
                    'MSVCR80.DLL', 'MSVCR71.DLL', 'CRYPT32.DLL', 'MSVBVM50.DLL', 'MFC42U.DLL', 'MSVCP71.DLL',
                    'MSVCRT.DLL', 'RPCRT4.DLL', 'MSVCP80.DLL', 'HAL.DLL', 'ATL80.DLL', 'SECUR32.DLL', 'NTOSKRNL.EXE',
                    'MSIMG32.DLL', 'GDIPLUS.DLL', 'SETUPAPI.DLL', 'ATL.DLL', 'CRTDLL.DLL', 'RTL70.BPL', 'VCL70.BPL',
                    'AVUTIL.DLL', 'IMM32.DLL', 'RASAPI32.DLL', 'MSVCP60.DLL']
        testapis = ['GETSYSTEMTIMEASFILETIME', 'QUERYPERFORMANCECOUNTER', 'GETMODULEFILENAMEW', 'LSTRLENW',
                    'REGOPENKEYEXW', 'LOADLIBRARYW', 'DISABLETHREADLIBRARYCALLS', 'GETMODULEHANDLEW',
                    'REGQUERYVALUEEXW', 'SETUNHANDLEDEXCEPTIONFILTER', 'INTERLOCKEDEXCHANGE',
                    'INTERLOCKEDCOMPAREEXCHANGE', 'WCSLEN', 'REGSETVALUEEXW', '_CORDLLMAIN', 'CREATEFILEW',
                    '_ADJUST_FDIV', '_INITTERM', '_WCSICMP', 'REGCREATEKEYEXW', 'FREE', 'GETVERSIONEXW',
                    'ISDEBUGGERPRESENT', 'CREATEEVENTW', 'LOADSTRINGW', 'REGDELETEKEYW', 'LSTRCMPIW', 'SENDMESSAGEW',
                    'WCSCHR', '_PURECALL']
        try:
            for f in self.pe.DIRECTORY_ENTRY_IMPORT:
                dlls.append(f.dll.upper())
                try:
                    for n in f.imports:
                        apis.append(n.name.upper())
                except:
                    continue
        except Exception:
            hi = 0
        for f in testdlls:
            if f in dlls:
                self.writeNum(1)
            else:
                self.writeNum(0)
        for f in testapis:
            if f in apis:
                self.writeNum(1)
            else:
                self.writeNum(0)

    def savesectioncount(self):
        self.writeNum(self.pe.DOS_HEADER.e_lfanew)
        tmp = []
        api = []
        try:
            for f in self.pe.DIRECTORY_ENTRY_IMPORT:
                tmp.append(f.dll.upper())
                try:
                    for n in f.imports:
                        api.append(n.name.upper())
                except:
                    continue
        except:
            tmp = []
            api = []
        self.writeNum(len(tmp))
        self.writeNum(len(api))

        tmp = []
        try:
            for f in self.pe.DIRECTORY_ENTRY_EXPORT.symbols:
                tmp.append(f)
        except:
            tmp = []
        self.writeNum(len(tmp))

        if (self.pe.FILE_HEADER.NumberOfSymbols == len(tmp)):
            self.writeNum(1)
        else:
            self.writeNum(0)

        tmp = []
        try:
            for f in self.pe.DIRECTORY_ENTRY_DEBUG:
                tmp.append(f)
        except:
            tmp = []
        self.writeNum(len(tmp))

        tmp = []
        try:
            for f in self.pe.DIRECTORY_ENTRY_BOUND_IMPORT:
                tmp.append(f)
        except:
            tmp = []
        self.writeNum(len(tmp))

        self.writeNum(len(self.pe.sections))

    def saveDosHeader(self):
        ##    self.writeNum(self.pe.DOS_HEADER.e_magic)
        ##    self.writeNum(self.pe.DOS_HEADER.e_cblp)
        ##    self.writeNum(self.pe.DOS_HEADER.e_cp)
        ##    self.writeNum(self.pe.DOS_HEADER.e_crlc)
        ##    self.writeNum(self.pe.DOS_HEADER.e_cparhdr)
        ##    self.writeNum(self.pe.DOS_HEADER.e_minalloc)
        ##    self.writeNum(self.pe.DOS_HEADER.e_maxalloc)
        ##    self.writeNum(self.pe.DOS_HEADER.e_ss)
        ##    self.writeNum(self.pe.DOS_HEADER.e_sp)
        ##    self.writeNum(self.pe.DOS_HEADER.e_csum)
        ##    self.writeNum(self.pe.DOS_HEADER.e_ip)
        ##    self.writeNum(self.pe.DOS_HEADER.e_cs)
        ##    self.writeNum(self.pe.DOS_HEADER.e_lfarlc)
        ##    self.writeNum(self.pe.DOS_HEADER.e_ovno)
        ##    self.writeNum(self.pe.DOS_HEADER.e_res)
        ##    self.writeNum(self.pe.DOS_HEADER.e_oemid)
        ##    self.writeNum(self.pe.DOS_HEADER.e_oeminfo)
        ##    self.writeNum(self.pe.DOS_HEADER.e_res2)
        self.writeNum(self.pe.DOS_HEADER.e_lfanew)

    def saveFILE_HEADER(self):
        # self.writeNum(self.pe.FILE_HEADER.Machine)
        self.writeNum(self.pe.FILE_HEADER.NumberOfSections)
        if (self.pe.FILE_HEADER.NumberOfSections == len(self.pe.sections)):
            self.writeNum(1)
        else:
            self.writeNum(0)
            # self.writeNum(self.pe.FILE_HEADER.TimeDateStamp)
        self.writeNum(self.pe.FILE_HEADER.PointerToSymbolTable)
        self.writeNum(self.pe.FILE_HEADER.NumberOfSymbols)
        self.writeNum(self.pe.FILE_HEADER.SizeOfOptionalHeader)
        self.writeNum(self.pe.FILE_HEADER.Characteristics)

    def saveOPTIONAL_HEADER(self):
        # 该部分和参考差了一项属性
        # self.writeNum(self.pe.OPTIONAL_HEADER.Magic)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MajorLinkerVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MinorLinkerVersion)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfCode)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfInitializedData)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfUninitializedData)
        self.writeNum(self.pe.OPTIONAL_HEADER.AddressOfEntryPoint)
        self.writeNum(self.pe.OPTIONAL_HEADER.BaseOfCode)
        self.writeNum(self.pe.OPTIONAL_HEADER.ImageBase)
        self.writeNum(self.pe.OPTIONAL_HEADER.SectionAlignment)
        self.writeNum(self.pe.OPTIONAL_HEADER.FileAlignment)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MajorOperatingSystemVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MinorOperatingSystemVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MajorImageVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MinorImageVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MajorSubsystemVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.MinorSubsystemVersion)
        # self.writeNum(self.pe.OPTIONAL_HEADER.Reserved1)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfImage)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfHeaders)
        self.writeNum(self.pe.OPTIONAL_HEADER.CheckSum)
        # self.writeNum(self.pe.OPTIONAL_HEADER.Subsystem)
        # self.writeNum(self.pe.OPTIONAL_HEADER.DllCharacteristics)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfStackReserve)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfStackCommit)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfHeapReserve)
        self.writeNum(self.pe.OPTIONAL_HEADER.SizeOfHeapCommit)
        self.writeNum(self.pe.OPTIONAL_HEADER.LoaderFlags)
        self.writeNum(self.pe.OPTIONAL_HEADER.NumberOfRvaAndSizes)
        self.writeNum(self.pe.OPTIONAL_HEADER.DllCharacteristics)

    def saveDATA_DIRECTORY(self):
        count = 0
        for f in self.pe.OPTIONAL_HEADER.DATA_DIRECTORY:
            self.writeNum(f.VirtualAddress)
            self.writeNum(f.Size)
            count += 1
        for m in range(count, 16):
            self.writeNum(0)
            self.writeNum(0)

    def saveSECTIONS_HEADER(self):
        have = 0
        k = 0
        for f in self.pe.sections:
            if f.Name.endswith(".text") or f.Name.endswith(".code"):
                self.writeNum(f.Misc)
                self.writeNum(f.Misc_PhysicalAddress)
                self.writeNum(f.Misc_VirtualSize)
                self.writeNum(f.VirtualAddress)
                self.writeNum(f.SizeOfRawData)
                self.writeNum(f.PointerToRawData)
                self.writeNum(f.PointerToRelocations)
                self.writeNum(f.PointerToLinenumbers)
                self.writeNum(f.NumberOfRelocations)
                self.writeNum(f.NumberOfLinenumbers)
                self.writeNum(f.Characteristics)
                have = 1
                break
        if have == 0:
            while k < 11:
                self.writeNum(0)
                k = k + 1
            k = 0
        else:
            have = 0
        for f in self.pe.sections:
            if f.Name.endswith(".data"):
                self.writeNum(f.Misc)
                self.writeNum(f.Misc_PhysicalAddress)
                self.writeNum(f.Misc_VirtualSize)
                self.writeNum(f.VirtualAddress)
                self.writeNum(f.SizeOfRawData)
                self.writeNum(f.PointerToRawData)
                self.writeNum(f.PointerToRelocations)
                self.writeNum(f.PointerToLinenumbers)
                self.writeNum(f.NumberOfRelocations)
                self.writeNum(f.NumberOfLinenumbers)
                self.writeNum(f.Characteristics)
                have = 1
                break
        if have == 0:
            while k < 11:
                self.writeNum(0)
                k = k + 1
            k = 0
        else:
            have = 0
        for f in self.pe.sections:
            if f.Name.endswith(".rsrc"):
                self.writeNum(f.Misc)
                self.writeNum(f.Misc_PhysicalAddress)
                self.writeNum(f.Misc_VirtualSize)
                self.writeNum(f.VirtualAddress)
                self.writeNum(f.SizeOfRawData)
                self.writeNum(f.PointerToRawData)
                self.writeNum(f.PointerToRelocations)
                self.writeNum(f.PointerToLinenumbers)
                self.writeNum(f.NumberOfRelocations)
                self.writeNum(f.NumberOfLinenumbers)
                self.writeNum(f.Characteristics)
                have = 1
                break
        if have == 0:
            while k < 11:
                self.writeNum(0)
                k = k + 1
            k = 0
        else:
            have = 0
        for f in self.pe.sections:
            if f.Name.endswith(".idata"):
                self.writeNum(f.Misc)
                self.writeNum(f.Misc_PhysicalAddress)
                self.writeNum(f.Misc_VirtualSize)
                self.writeNum(f.VirtualAddress)
                self.writeNum(f.SizeOfRawData)
                self.writeNum(f.PointerToRawData)
                self.writeNum(f.PointerToRelocations)
                self.writeNum(f.PointerToLinenumbers)
                self.writeNum(f.NumberOfRelocations)
                self.writeNum(f.NumberOfLinenumbers)
                self.writeNum(f.Characteristics)
                have = 1
                break
        if have == 0:
            while k < 11:
                self.writeNum(0)
                k = k + 1
            k = 0
        else:
            have = 0
        for f in self.pe.sections:
            if f.Name.endswith(".edata"):
                self.writeNum(f.Misc)
                self.writeNum(f.Misc_PhysicalAddress)
                self.writeNum(f.Misc_VirtualSize)
                self.writeNum(f.VirtualAddress)
                self.writeNum(f.SizeOfRawData)
                self.writeNum(f.PointerToRawData)
                self.writeNum(f.PointerToRelocations)
                self.writeNum(f.PointerToLinenumbers)
                self.writeNum(f.NumberOfRelocations)
                self.writeNum(f.NumberOfLinenumbers)
                self.writeNum(f.Characteristics)
                have = 1
                break
        if have == 0:
            while k < 11:
                self.writeNum(0)
                k = k + 1
            k = 0
        else:
            have = 0

    def saveRESOURCE_DIRECTORY(self):
        types = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 16, 17, 19, 20, 21, 22, 23, 24]
        k = 0
        try:
            self.writeNum((
                self.pe.DIRECTORY_ENTRY_RESOURCE.struct.NumberOfNamedEntries + self.pe.DIRECTORY_ENTRY_RESOURCE.struct.NumberOfIdEntries))
            k = 1
            for t in types:
                be = False
                for f in self.pe.DIRECTORY_ENTRY_RESOURCE.entries:
                    if f.id == t:
                        self.writeNum((f.directory.struct.NumberOfNamedEntries + f.directory.struct.NumberOfIdEntries))
                        be = True
                if not be:
                    self.writeNum(0)
                k = k + 1
        except Exception:
            while k < 22:
                self.writeNum(0)
                k = k + 1

    def createPEarff(self, filename):
        self.pcount = 0
        self.res = ""
        self.pe = ""
        try:
            self.pe = pefile.PE(filename)
        except Exception:
            print filename + " is not a pe file!"
            return ""
        if self.pe.DOS_HEADER.e_magic == 0x5A4D and self.pe.NT_HEADERS.Signature == 0x00004550:
            self.saveDIRECTORY_ENTRY_IMPORT()
            self.savesectioncount()
            self.saveFILE_HEADER()
            self.saveOPTIONAL_HEADER()
            self.saveDATA_DIRECTORY()
            self.saveSECTIONS_HEADER()
            self.saveRESOURCE_DIRECTORY()
        return self.res

    def createHeader(self, filename):
        ofile = open(filename, "wb")
        writeline(ofile, "@relation malware")
        count = 1
        while count <= 201:
            writeline(ofile, "@attribute a" + str(count) + " real")
            count = count + 1
        writeline(ofile, "@attribute result{malware,benign}")
        writeline(ofile, "@data")
        ofile.close()


#ex = extractPE()
#print ex.createPEarff("D:/tlsh.dll")
