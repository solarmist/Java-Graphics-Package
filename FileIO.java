
import java.io.*;
import java.util.logging.Logger;
//import jwo.landserf.LandSerf;

public class FileIO
{

    public static String errorMessage;
    public static long filePointer;
    public static int lineNumber;
    public static int bitsPerValue;
    public static int byteOrder;
    public static float nullCode;
    public static boolean substituteNull;
    public static float zMultiplier;
    public static boolean useMultiplier;
    public static final int LOAD_ALL = 1;
    public static final int LOAD_SELECTED = 2;
    public static final int GATHER_INFO = 3;
    public static final int BIG_ENDIAN = 1;
    public static final int LITTLE_ENDIAN = 2;

    public FileIO()
    {
    }

    public static void reset()
    {
        errorMessage = null;
        filePointer = 0L;
        lineNumber = 0;
        bitsPerValue = 0;
        byteOrder = 0;
        nullCode = (0.0F / 0.0F);
        zMultiplier = 1.0F;
        substituteNull = false;
        useMultiplier = false;
    }

    public static String getErrorMessage()
    {
        return errorMessage;
    }

    public static void skip(InputStream is, long numBytes)
    {
        try
        {
            for(int i = 0; (long)i < numBytes; i++)
            {
                is.read();
            }

            filePointer += numBytes;
        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
    }

    public static void skip(RandomAccessFile raf, int numBytes)
    {
        try
        {
            raf.skipBytes(numBytes);
            filePointer += numBytes;
        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
    }

    public static void seek(RandomAccessFile raf, long numBytes)
    {
        try
        {
            raf.seek(numBytes);
            filePointer = numBytes;
        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
    }

    public static byte readByte(InputStream is)
    {
        try
        {
            byte byteValue = (byte)is.read();
            filePointer++;
            return byteValue;
        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        return 0;
    }

    public static byte readByte(RandomAccessFile raf)
    {
        try
        {
            byte byteValue = raf.readByte();
            filePointer++;
            return byteValue;
        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        return 0;
    }

    public static short readShort(RandomAccessFile raf)
    {
        if(byteOrder == 2)
        {
            return readShortLittleEndian(raf);
        }
        if(byteOrder == 1)
        {
            return readShortBigEndian(raf);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0;
        }
    }

    public static short readShort(InputStream is)
    {
        if(byteOrder == 2)
        {
            return readShortLittleEndian(is);
        }
        if(byteOrder == 1)
        {
            return readShortBigEndian(is);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0;
        }
    }

    public static boolean writeByte(byte value, OutputStream os)
    {
        try
        {
            byte bytes[] = {
                value
            };
            filePointer++;
            os.write(bytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write byte to output stream: " + e);
        }
        return true;
    }

    public static boolean writeShort(short value, OutputStream os)
    {
        if(byteOrder == 2)
        {
            return writeShortLittleEndian(value, os);
        }
        if(byteOrder == 1)
        {
            return writeShortBigEndian(value, os);
        } else
        {
            System.out.println("Byte order not specified.");
            return false;
        }
    }

    public static boolean writeFloat(float value, OutputStream os)
    {
        if(byteOrder == 2)
        {
            return writeFloatLittleEndian(value, os);
        }
        if(byteOrder == 1)
        {
            return writeFloatBigEndian(value, os);
        } else
        {
            System.out.println("Byte order not specified.");
            return false;
        }
    }

    public static boolean writeDouble(double value, OutputStream os)
    {
        if(byteOrder == 2)
        {
            return writeDoubleLittleEndian(value, os);
        }
        if(byteOrder == 1)
        {
            return writeDoubleBigEndian(value, os);
        } else
        {
            System.out.println("Byte order not specified.");
            return false;
        }
    }

    public static int readInt(RandomAccessFile raf)
    {
        if(byteOrder == 2)
        {
            return readIntLittleEndian(raf);
        }
        if(byteOrder == 1)
        {
            return readIntBigEndian(raf);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0;
        }
    }

    public static int readInt(InputStream is)
    {
        if(byteOrder == 2)
        {
            return readIntLittleEndian(is);
        }
        if(byteOrder == 1)
        {
            return readIntBigEndian(is);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0;
        }
    }

    public static boolean writeInt(int value, OutputStream os)
    {
        if(byteOrder == 2)
        {
            return writeIntLittleEndian(value, os);
        }
        if(byteOrder == 1)
        {
            return writeIntBigEndian(value, os);
        } else
        {
            System.out.println("Byte order not specified.");
            return false;
        }
    }

    public static float readFloat(RandomAccessFile raf)
    {
        if(byteOrder == 2)
        {
            return readFloatLittleEndian(raf);
        }
        if(byteOrder == 1)
        {
            return readFloatBigEndian(raf);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0.0F;
        }
    }

    public static float readFloat(InputStream is)
    {
        if(byteOrder == 2)
        {
            return readFloatLittleEndian(is);
        }
        if(byteOrder == 1)
        {
            return readFloatBigEndian(is);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0.0F;
        }
    }

    public static double readDouble(RandomAccessFile raf)
    {
        if(byteOrder == 2)
        {
            return readDoubleLittleEndian(raf);
        }
        if(byteOrder == 1)
        {
            return readDoubleBigEndian(raf);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0.0D;
        }
    }

    public static double readDouble(InputStream is)
    {
        if(byteOrder == 2)
        {
            return readDoubleLittleEndian(is);
        }
        if(byteOrder == 1)
        {
            return readDoubleBigEndian(is);
        } else
        {
            System.out.println("Byte order not specified.");
            return 0.0D;
        }
    }

    public static short readShortLittleEndian(RandomAccessFile raf)
    {
        try
        {
            int low = raf.read() & 0xff;
            int high = raf.read() & 0xff;
            filePointer += 2L;
            return (short)(high << 8 | low);
        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        return 0;
    }

    public static short readShortLittleEndian(InputStream is)
    {
        try
        {
            int low = is.read() & 0xff;
            int high = is.read() & 0xff;
            filePointer += 2L;
            return (short)(high << 8 | low);
        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        return 0;
    }

    public static boolean writeShortLittleEndian(short value, OutputStream os)
    {
        try
        {
            byte shortBytes[] = new byte[2];
            shortBytes[0] = (byte)(value & 0xff);
            shortBytes[1] = (byte)(value >> 8 & 0xff);
            filePointer += 2L;
            os.write(shortBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write 16 bit word to output stream: " + e);
        }
        return true;
    }

    public static int readIntLittleEndian(RandomAccessFile raf)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 0; shiftBy < 32; shiftBy += 8)
            {
                accum |= (raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 4L;
        return accum;
    }

    public static int readIntLittleEndian(InputStream is)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 0; shiftBy < 32; shiftBy += 8)
            {
                accum |= (is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 4L;
        return accum;
    }

    public static boolean writeIntLittleEndian(int value, OutputStream os)
    {
        try
        {
            byte intBytes[] = new byte[4];
            intBytes[0] = (byte)(value & 0xff);
            intBytes[1] = (byte)(value >> 8 & 0xff);
            intBytes[2] = (byte)(value >> 16 & 0xff);
            intBytes[3] = (byte)(value >> 24 & 0xff);
            filePointer += 4L;
            os.write(intBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write 32 bit word to output stream: " + e);
        }
        return true;
    }

    public static float readFloatLittleEndian(RandomAccessFile raf)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 0; shiftBy < 32; shiftBy += 8)
            {
                accum |= (raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 4L;
        return Float.intBitsToFloat(accum);
    }

    public static float readFloatLittleEndian(InputStream is)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 0; shiftBy < 32; shiftBy += 8)
            {
                accum |= (is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 4L;
        return Float.intBitsToFloat(accum);
    }

    public static boolean writeFloatBigEndian(float value, OutputStream os)
    {
        try
        {
            int floatBits = Float.floatToIntBits(value);
            byte floatBytes[] = new byte[4];
            floatBytes[3] = (byte)(floatBits & 0xff);
            floatBytes[2] = (byte)(floatBits >> 8 & 0xff);
            floatBytes[1] = (byte)(floatBits >> 16 & 0xff);
            floatBytes[0] = (byte)(floatBits >> 24 & 0xff);
            filePointer += 4L;
            os.write(floatBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write binary stream: " + e);
            return false;
        }
        return true;
    }

    public static boolean writeFloatLittleEndian(float value, OutputStream os)
    {
        try
        {
            int floatBits = Float.floatToIntBits(value);
            byte floatBytes[] = new byte[4];
            floatBytes[0] = (byte)(floatBits & 0xff);
            floatBytes[1] = (byte)(floatBits >> 8 & 0xff);
            floatBytes[2] = (byte)(floatBits >> 16 & 0xff);
            floatBytes[3] = (byte)(floatBits >> 24 & 0xff);
            filePointer += 4L;
            os.write(floatBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write binary stream: " + e);
            return false;
        }
        return true;
    }

    public static boolean writeDoubleBigEndian(double value, OutputStream os)
    {
        try
        {
            long doubleBits = Double.doubleToLongBits(value);
            byte doubleBytes[] = new byte[8];
            doubleBytes[7] = (byte)(int)(doubleBits & 255L);
            doubleBytes[6] = (byte)(int)(doubleBits >> 8 & 255L);
            doubleBytes[5] = (byte)(int)(doubleBits >> 16 & 255L);
            doubleBytes[4] = (byte)(int)(doubleBits >> 24 & 255L);
            doubleBytes[3] = (byte)(int)(doubleBits >> 32 & 255L);
            doubleBytes[2] = (byte)(int)(doubleBits >> 40 & 255L);
            doubleBytes[1] = (byte)(int)(doubleBits >> 48 & 255L);
            doubleBytes[0] = (byte)(int)(doubleBits >> 56 & 255L);
            filePointer += 8L;
            os.write(doubleBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write binary stream: " + e);
            return false;
        }
        return true;
    }

    public static boolean writeDoubleLittleEndian(double value, OutputStream os)
    {
        try
        {
            long doubleBits = Double.doubleToLongBits(value);
            byte doubleBytes[] = new byte[8];
            doubleBytes[0] = (byte)(int)(doubleBits & 255L);
            doubleBytes[1] = (byte)(int)(doubleBits >> 8 & 255L);
            doubleBytes[2] = (byte)(int)(doubleBits >> 16 & 255L);
            doubleBytes[3] = (byte)(int)(doubleBits >> 24 & 255L);
            doubleBytes[4] = (byte)(int)(doubleBits >> 32 & 255L);
            doubleBytes[5] = (byte)(int)(doubleBits >> 40 & 255L);
            doubleBytes[6] = (byte)(int)(doubleBits >> 48 & 255L);
            doubleBytes[7] = (byte)(int)(doubleBits >> 56 & 255L);
            filePointer += 8L;
            os.write(doubleBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write binary stream: " + e);
            return false;
        }
        return true;
    }

    public static double readDoubleLittleEndian(RandomAccessFile raf)
    {
        long accum = 0L;
        try
        {
            for(int shiftBy = 0; shiftBy < 64; shiftBy += 8)
            {
                accum |= (long)(raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 8L;
        return Double.longBitsToDouble(accum);
    }

    public static double readDoubleLittleEndian(InputStream is)
    {
        long accum = 0L;
        try
        {
            for(int shiftBy = 0; shiftBy < 64; shiftBy += 8)
            {
                accum |= (long)(is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 8L;
        return Double.longBitsToDouble(accum);
    }

    public static short readShortBigEndian(RandomAccessFile raf)
    {
        try
        {
            int high = raf.read() & 0xff;
            int low = raf.read() & 0xff;
            filePointer += 2L;
            return (short)(high << 8 | low);
        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        return 0;
    }

    public static short readShortBigEndian(InputStream is)
    {
        try
        {
            int high = is.read() & 0xff;
            int low = is.read() & 0xff;
            filePointer += 2L;
            return (short)(high << 8 | low);
        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        return 0;
    }

    public static boolean writeShortBigEndian(short value, OutputStream os)
    {
        try
        {
            byte shortBytes[] = new byte[2];
            shortBytes[0] = (byte)(value >> 8 & 0xff);
            shortBytes[1] = (byte)(value & 0xff);
            filePointer += 2L;
            os.write(shortBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write 16 bit word to output stream: " + e);
        }
        return true;
    }

    public static int readIntBigEndian(RandomAccessFile raf)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 24; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 4L;
        return accum;
    }

    public static int readIntBigEndian(InputStream is)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 24; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 4L;
        return accum;
    }

    public static boolean writeIntBigEndian(int value, OutputStream os)
    {
        try
        {
            byte intBytes[] = new byte[4];
            intBytes[0] = (byte)(value >> 24 & 0xff);
            intBytes[1] = (byte)(value >> 16 & 0xff);
            intBytes[2] = (byte)(value >> 8 & 0xff);
            intBytes[3] = (byte)(value & 0xff);
            filePointer += 4L;
            os.write(intBytes);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write 32 bit word to output stream: " + e);
        }
        return true;
    }

    public static float readFloatBigEndian(RandomAccessFile raf)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 24; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 4L;
        return Float.intBitsToFloat(accum);
    }

    public static float readFloatBigEndian(InputStream is)
    {
        int accum = 0;
        try
        {
            for(int shiftBy = 24; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 4L;
        return Float.intBitsToFloat(accum);
    }

    public static double readDoubleBigEndian(RandomAccessFile raf)
    {
        long accum = 0L;
        try
        {
            for(int shiftBy = 56; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (long)(raf.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read random access file: " + e);
        }
        filePointer += 8L;
        return Double.longBitsToDouble(accum);
    }

    public static double readDoubleBigEndian(InputStream is)
    {
        long accum = 0L;
        try
        {
            for(int shiftBy = 56; shiftBy >= 0; shiftBy -= 8)
            {
                accum |= (long)(is.read() & 0xff) << shiftBy;
            }

        }
        catch(IOException e)
        {
            System.out.println("Cannot read binary stream: " + e);
        }
        filePointer += 8L;
        return Double.longBitsToDouble(accum);
    }

    public static boolean writeLine(String text, BufferedWriter outFile)
    {
        try
        {
            outFile.write(text, 0, text.length());
            outFile.newLine();
            lineNumber++;
        }
        catch(IOException e)
        {
            System.out.println("Cannot write text output: " + e);
            return false;
        }
        return true;
    }

    public static String readCharacters(int numChars, BufferedReader inFile)
    {
        try
        {
            char chars[] = new char[numChars];
            inFile.read(chars);
            return new String(chars);
        }
        catch(IOException e)
        {
            System.out.println("Cannot read character input: " + e);
        }
        return null;
    }

    public static boolean writeCharacters(String text, OutputStream os)
    {
        try
        {
            byte characters[] = new byte[text.length()];
            for(int i = 0; i < characters.length; i++)
            {
                characters[i] = (byte)text.charAt(i);
            }

            filePointer += characters.length;
            os.write(characters);
        }
        catch(IOException e)
        {
            System.out.println("Cannot write characters to output stream: " + e);
        }
        return true;
    }

    public static String readNextWord(BufferedReader inFile)
    {
        try
        {
            StringBuffer word = new StringBuffer();
            boolean foundWord = false;
            char character[] = new char[1];
            while(!foundWord) 
            {
                if(inFile.read(character) < 0)
                {
                    return null;
                }
                if(character[0] != ' ' && character[0] != '\t' && character[0] != '\n' && character[0] != '\f')
                {
                    foundWord = true;
                }
            }
            word.append(character[0]);
            while(foundWord) 
            {
                if(inFile.read(character) < 0)
                {
                    break;
                }
                if(character[0] == ' ' || character[0] == '\t' || character[0] == '\n' || character[0] == '\f')
                {
                    foundWord = false;
                } else
                {
                    word.append(character[0]);
                }
            }
            return word.toString();
        }
        catch(IOException e)
        {
            System.out.println("Cannot read character input: " + e);
        }
        return null;
    }
}
