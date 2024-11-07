export type DocumentMetadata = {
  fileName: string;
  documentId: string;
  source: string;
  distance: number;
};

export type ResponseData = {
  answer: string;
  documentMetadata: DocumentMetadata[];
};
