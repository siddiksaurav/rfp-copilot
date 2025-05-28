import formidable from 'formidable';

export const config = {
  api: {
    bodyParser: false,
  },
};

export default async function handler(req, res) {
  if (req.method === 'POST') {
    const form = new formidable.IncomingForm();

    form.parse(req, (err, fields, files) => {
      if (err) {
        console.error('Error parsing the file:', err);
        return res.status(500).json({ error: 'Error parsing the file' });
      }

      // Simulate validation logic
      const results = [
        { item: 'Item 1', valid: true, reason: '' },
        { item: 'Item 2', valid: false, reason: 'Invalid format' },
        { item: 'Item 3', valid: true, reason: '' },
      ];

      return res.status(200).json({ results });
    });
  } else {
    res.setHeader('Allow', ['POST']);
    res.status(405).end(`Method ${req.method} Not Allowed`);
  }
}