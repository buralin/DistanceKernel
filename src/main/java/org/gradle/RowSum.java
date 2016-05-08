package org.gradle;
import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxDestroy;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;
 
import java.util.Locale;
 
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;

public class RowSum {
    public static void main(String[] args)
    {
        // Enable exceptions and omit subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);
 
        // Create a context for the first device
        cuInit(0);
        CUcontext context = new CUcontext();
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        cuCtxCreate(context, 0, device);
 
        // Load the module and obtain the pointer to the kernel function
        CUmodule module = new CUmodule();
        cuModuleLoad(module, "JCudaMatrixRowSumKernel.ptx");
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "rowSums");
       
        // Create the input matrix in host memory
        int rows = 8;
        int cols = 6;
        float hostMatrix[] = createExampleMatrix(rows, cols);
       
        System.out.println("Input matrix:");
        System.out.println(createString2D(hostMatrix, rows, cols));
       
        // Copy the host data to the device
        CUdeviceptr deviceMatrix = new CUdeviceptr();
        cuMemAlloc(deviceMatrix, rows * cols * Sizeof.FLOAT);
        cuMemcpyHtoD(deviceMatrix, Pointer.to(hostMatrix),
            rows * cols * Sizeof.FLOAT);
 
        // Allocate memory for the result on the device
        CUdeviceptr deviceSums = new CUdeviceptr();
        cuMemAlloc(deviceSums, rows * Sizeof.FLOAT);
       
        // Set up and launch the kernel
        Pointer kernelParameters = Pointer.to(
            Pointer.to(deviceMatrix),
            Pointer.to(deviceSums),
            Pointer.to(new int[]{ rows }),
            Pointer.to(new int[]{ cols })
        );
        int blockSizeX = 256;
        int gridSizeX = (rows * cols + blockSizeX - 1) / blockSizeX;
        cuLaunchKernel(function,
            gridSizeX, 1, 1,
            blockSizeX, 1, 1,
            0, null, kernelParameters, null);
       
        // Copy the result from the device to the host
        float hostSums[] = new float[rows];
        cuMemcpyDtoH(Pointer.to(hostSums), deviceSums, rows * Sizeof.FLOAT);
 
        System.out.println("Row sums:");
        System.out.println(createString2D(hostSums, rows, 1));
       
        // Clean up
        cuMemFree(deviceSums);
        cuMemFree(deviceMatrix);
        cuCtxDestroy(context);
    }
 
    private static float[] createExampleMatrix(int rows, int cols)
    {
        float matrix[] = new float[rows*cols];
        int counter = 0;
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                matrix[r * cols + c] = counter;
                counter++;
            }
        }
        return matrix;
    }
   
    private static String createString2D(
        float matrix[], int rows, int cols)
    {
        String format = "%7.2f";
        StringBuffer sb = new StringBuffer();
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                float value = matrix[r * cols + c];
                String s = String.format(Locale.ENGLISH, format, value);
                sb.append(s).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
