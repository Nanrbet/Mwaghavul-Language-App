import pandas as pd
import csv

gloss_duplication = False

def process_gloss(gloss, delimiter):
    """Process the gloss field and split by delimiter."""
    # parts = [part.strip() + (delimiter if part.strip().isalnum() and delimiter == '?' else '') for part in gloss.split(delimiter)]
    parts = []
    for part in gloss.split(delimiter):
        if part.strip() and delimiter == '?':
            parts.append(part.strip() + delimiter)
        elif part.strip():
            parts.append(part.strip())
    return parts

# def read_csv(file_path): # this is for duplication of gloss(normal reading)
#     """Read the CSV file and return rows."""
#     with open(file_path, mode='r', encoding='utf-8') as infile:
#         reader = csv.DictReader(infile)
#         rows = list(reader)
#     return rows
def read_csv(file_path): # TO read the csv of the english dictionary with space
    rows = []
    encodings = ['utf-8', 'latin1', 'iso-8859-1', 'cp1252']

    for encoding in encodings:
        try:
            with open(file_path, mode='r', encoding=encoding) as infile:
                reader = csv.reader((line for line in infile if line.strip()), delimiter=',')
                header = next(reader)  # Skip header
                for row in reader:
                    if any(field.strip() for field in row):  # Skip empty rows
                        rows.append(row)
            break
        except UnicodeDecodeError:
            print(f"Encoding {encoding} failed. Trying the next encoding...")

    return rows

def write_csv(file_path, rows, fieldnames):
    """Write the rows to a CSV file."""
    with open(file_path, mode='w', newline='', encoding='latin1') as outfile:
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

def process_rows(rows):
    """Process each row to handle multiple gloss entries."""
    new_rows = []
    for row in rows:
        if row['pl'].strip() == 'mo':
            row['pl'] = ''
        if gloss_duplication: # to only execute if we need to split the gloss
            gloss = row['Gloss']
            if ';' in gloss:
                gloss_parts = process_gloss(gloss, ';')
            elif '?' in gloss:
                gloss_parts = process_gloss(gloss, '?')
            else:
                new_rows.append(row)
                continue

            for part in gloss_parts:
                new_row = row.copy()
                new_row['Gloss'] = part
                new_rows.append(new_row)
    return new_rows

def mwaghavul_dictionary_edit():
    # TESTS: 
    # process_gloss('how? why?', '?')
    # process_gloss('be careful!; be watchful!; Look out!', ';')
    input_file = 'Mwaghavul_dic.csv'
    output_file = 'mwa_eng.csv'

    rows = read_csv(input_file)
    new_rows = process_rows(rows)
    fieldnames = rows[0].keys()

    write_csv(output_file, new_rows, fieldnames)
    print(f"\nProcessing complete. Check '{output_file}' for results.")

def extract_english_dic():
    output_file = 'eng_dic.csv'
    all_new_rows = []
    fieldnames = ['Word', 'PoS', 'Definition', 'Examples']

    for letter in "ABCDEFGHIJKLMNOPQRSTUVWXYZ":
        input_file = f'Dictionary in csv/{letter}.csv'
        try:
            rows = read_csv(input_file)
            new_rows = process_dic_rows(rows)
            all_new_rows.extend(new_rows)
        except FileNotFoundError:
            print(f"File {input_file} not found. Skipping.")
            
    write_csv(output_file, all_new_rows, fieldnames)
    print(f"\nProcessing complete. Check '{output_file}' for results.")

def process_dic_rows(rows):
    """Process each row to handle word, PoS, and definition extraction from ENGLISH DICTIONARY."""
    new_rows = []
    for row in rows:
        entry = row[0]  # Assuming the data is in the first column
        word = ""
        pos = ""
        definition = ""

        # Extract word, PoS, and definition
        open_paren_index = entry.find('(')
        if open_paren_index != -1:
            word = entry[:open_paren_index].strip()

            close_paren_index = entry.find(')', open_paren_index)
            if close_paren_index != -1:
                pos = entry[open_paren_index + 1:close_paren_index].strip()
                definition = entry[close_paren_index + 1:].strip()
            else:
                definition = entry[open_paren_index + 1:].strip()

        else:
            word = entry.strip()

        new_rows.append({'Word': word, 'PoS': pos, 'Definition': definition})

    return new_rows

# def process_dic_rows(rows):
#     """Process each row to handle multiple gloss entries from MWAGHAVUL DICTIONARY."""
#     for each row in inputfile:
#         e.g L () As a numeral, L stands for fifty in the English, as in the Latin language.
#         Start reading the row and if it is before the first open bracket '('
#         then add that to the Word (column 1) of the row and then anything inside that bracket is the PoS(column 2)
#         and then anything after the closing brackes of the Pos (column 2) is the definition
#         then add it to the rows
#     then append the rows to the new_dic_rows and return it to be saved as an csv

def main():
    # mwaghavul_dictionary_edit()
    extract_english_dic()

if __name__ == "__main__":
    main()

