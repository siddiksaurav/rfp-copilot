import FileUpload from "@/components/FileUpload";

export default function Home() {
  return (
    <main className="min-h-screen p-8">
      <div className="max-w-6xl mx-auto space-y-8">
        <div className="text-center space-y-2">
          <h1 className="text-4xl font-bold">RFP Document Analyzer</h1>
          <p className="text-lg text-muted-foreground">
            Upload your Terms of Reference (TOR) document for automatic validation and analysis
          </p>
        </div>
        <FileUpload />
      </div>
    </main>
  );
}
