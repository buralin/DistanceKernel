package org.gradle;

import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;

import java.io.IOException;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;

public class DistanceKernel2_2 {
	 public static void main(String args[]) throws IOException
	    {
	        // Enable exceptions and omit all subsequent error checks
	        JCudaDriver.setExceptionsEnabled(true);
	        
	        // Initialize the driver and create a context for the first device.
	        cuInit(0);
	        CUdevice device = new CUdevice();
	        cuDeviceGet(device, 0);
	        CUcontext context = new CUcontext();
	        cuCtxCreate(context, 0, device);
	        
	        
	        // Load the ptx file.
	        CUmodule module = new CUmodule();
	        cuModuleLoad(module, "Distance2_2.ptx");
	        // Obtain a function pointer to the "add" function.
	        CUfunction function = new CUfunction();
	        cuModuleGetFunction(function, module, "dist2_2");
	        
	        
	        int rows = 3;
	        int columns = 10000;
	        int elements = rows*columns;
	        
	        float [] in1 = new float [elements];
	        float [] in2 = new float [rows];
	        
	        
	        for (int i = 0; i < elements ;i++)
	        {
				in1[i]=1;
			}
	        
	        
	        // Allocate the device input data, and copy the
	        // host input data to the device
	        CUdeviceptr d_in1 = new CUdeviceptr();
	        cuMemAlloc(d_in1, elements * Sizeof.FLOAT);
	        cuMemcpyHtoD(d_in1, Pointer.to(in1),elements * Sizeof.FLOAT);
	        
	        
	        CUdeviceptr d_in2 = new CUdeviceptr();
	        cuMemAlloc(d_in2, rows * Sizeof.FLOAT);
	        cuMemcpyHtoD(d_in2, Pointer.to(in2),rows * Sizeof.FLOAT);
	        
	        
	        CUdeviceptr d_out = new CUdeviceptr();
	        cuMemAlloc(d_out, columns * Sizeof.FLOAT);
	        
	        // Set up the kernel parameters: A pointer to an array
	        // of pointers which point to the actual values.
	        Pointer kernelParameters = Pointer.to(Pointer.to(d_in1),Pointer.to(d_in2),Pointer.to(d_out),
	        Pointer.to(new int[]{rows}),Pointer.to(new int[]{columns}));
	        
	        // Call the kernel function.
	        int blockSizeX = 32;
	        int blockSizeY = 32;
	        int gridSizeX = (elements +blockSizeY -1)/blockSizeX;
	        cuLaunchKernel(function,
	            gridSizeX,  1, 1,               // Grid dimension
	            blockSizeX, blockSizeY, 1,      // Block dimension
	            0, null,                        // Shared memory size and stream
	            kernelParameters, null          // Kernel- and extra parameters
	        );
	        
	        
	        // Allocate host output memory and copy the device output
	        // to the host.
	        float out [] = new float [columns];
	        cuMemcpyDtoH(Pointer.to(out), d_out, columns * Sizeof.FLOAT);
	        
	        
	        for (int h = 0; h < out.length;h++)
			{
				System.out.println("DISTANCE *********** " + out[h]);
			}
	        
	        
	        cuMemFree(d_in1);
	        cuMemFree(d_in2);
	        cuMemFree(d_out);
    }
}
