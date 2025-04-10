import tkinter as tk
from tkinter import filedialog
import json
from components.sector import Sector
from components.seat import Seat

sectors = []
selected_item = None
current_tool = None
click_points = []
SEAT_CIRCLE_RANGE = 5

def set_tool(tool_name):
    global current_tool, click_points
    current_tool = tool_name
    click_points = []
    update_button_state()

def update_button_state():
    for button in menu_buttons:
        if button['text'].lower() == current_tool:
            button.config(bg="lightblue")
        else:
            button.config(bg="lightgray")

def clear_form():
    pass  # Nothing to clear since form is now in the popup

def on_canvas_click(event):
    global click_points, selected_item

    x, y = event.x, event.y

    if current_tool in ['stage', 'sector']:
        click_points.append((x, y))
        if len(click_points) == 2:
            x1, y1 = click_points[0]
            x2, y2 = click_points[1]

            name = "Stage" if current_tool == 'stage' else f"Sector {len(sectors)+1}"
            is_stage = (current_tool == 'stage')

            color = "gray" if is_stage else ""
            outline = "" if is_stage else "red"
            canvas.create_rectangle(x1, y1, x2, y2, fill=color, outline=outline, width=2)

            sector = Sector(name, min(x1,x2), min(y1,y2), max(x1,x2), max(y1,y2), is_stage)
            sectors.append(sector)

            click_points = []

    elif current_tool == 'seat':
        for sector in sectors:
            if not sector.is_stage:
                if sector.x_sx <= x <= sector.x_dx and sector.y_sx <= y <= sector.y_dx:
                    canvas.create_oval(x - SEAT_CIRCLE_RANGE,
                                        y - SEAT_CIRCLE_RANGE,
                                          x + SEAT_CIRCLE_RANGE,
                                            y + SEAT_CIRCLE_RANGE, fill="red")
                    seat = Seat(description="Unnumbered", x=x, y=y)  # Default to "Unnumbered"
                    sector.seats.append(seat)
                    selected_item = seat
                    show_item_details(selected_item)
                    break

def on_canvas_right_click(event):
    global selected_item
    x, y = event.x, event.y

    if current_tool == 'stage':
        for sector in sectors:
            if sector.is_stage and sector.x_sx <= x <= sector.x_dx and sector.y_sx <= y <= sector.y_dx:
                selected_item = sector
                show_item_details(selected_item)
                break
    elif current_tool == 'sector':
        for sector in sectors:
            if not sector.is_stage and sector.x_sx <= x <= sector.x_dx and sector.y_sx <= y <= sector.y_dx:
                selected_item = sector
                show_item_details(selected_item)
                break
    elif current_tool == 'seat':
        for sector in sectors:
            for seat in sector.seats:
                if seat.x - SEAT_CIRCLE_RANGE <= x <= seat.x + SEAT_CIRCLE_RANGE and seat.y - SEAT_CIRCLE_RANGE <= y <= seat.y + SEAT_CIRCLE_RANGE:
                    selected_item = seat
                    show_item_details(selected_item)
                    break

def show_item_details(item):
    # Crea un popup per la modifica
    popup = tk.Toplevel(root)
    popup.title("Modify Item")

    form_frame = tk.Frame(popup, bg="lightblue")
    form_frame.pack(side="top", fill="x", padx=10, pady=10)

    # Frame per il nome del settore
    name_frame = tk.Frame(form_frame)
    name_frame.pack(side="top", fill="x", padx=5, pady=5)

    if isinstance(item, Sector):
        name_label = tk.Label(name_frame, text="Sector Name:")
        name_label.pack(side="left", padx=5, pady=5)
        name_entry = tk.Entry(name_frame)
        name_entry.insert(0, item.name)
        name_entry.pack(side="left", padx=5, pady=5)

        update_button = tk.Button(name_frame, text="Update Sector", command=lambda: update_sector(item, name_entry.get(), popup))
        update_button.pack(side="left", padx=10)

        # Se il settore non è uno stage, aggiungi la sezione per i posti
        if not item.is_stage:
            seat_frame = tk.Frame(form_frame)
            seat_frame.pack(side="top", fill="x", padx=5, pady=5)

            num_seats_label = tk.Label(seat_frame, text="Number of Seats to Add:")
            num_seats_label.pack(side="left", padx=5, pady=5)
            num_seats_entry = tk.Entry(seat_frame)
            num_seats_entry.pack(side="left", padx=5, pady=5)

            add_seats_button = tk.Button(seat_frame, text="Add Seats", command=lambda: add_seats_to_sector(item, num_seats_entry.get()))
            add_seats_button.pack(side="left", padx=10)

            # Checkbox per numerare i posti
            numbering_var = tk.BooleanVar()
            numbering_checkbox = tk.Checkbutton(seat_frame, text="Number Seats Automatically", variable=numbering_var)
            numbering_checkbox.pack(side="left", padx=10)

            # Modifica il comando per includere la numerazione automatica
            def add_seats_with_numbering():
                add_seats_to_sector(item, num_seats_entry.get(), numbering_var.get())
            
            add_seats_button.config(command=add_seats_with_numbering)

        # Bottone per la cancellazione del settore
        delete_button = tk.Button(popup, text="Delete Sector", command=lambda: delete_sector(item, popup), bg="red", fg="white")
        delete_button.pack(pady=10)

    elif isinstance(item, Seat):
        name_label = tk.Label(name_frame, text="Seat description:")
        name_label.pack(side="left", padx=5, pady=5)
        name_entry = tk.Entry(name_frame)
        name_entry.insert(0, item.description)
        name_entry.pack(side="left", padx=5, pady=5)

        update_button = tk.Button(name_frame, text="Update Seat", command=lambda: update_seat(item, name_entry.get(), popup))
        update_button.pack(side="left", padx=10)
        
        delete_button = tk.Button(popup, text="Delete Sector", command=lambda: delete_seat(item, popup), bg="red", fg="white")
        delete_button.pack(pady=10)


def update_sector(sector, new_name, popup):
    sector.name = new_name
    selected_item = sector
    popup.destroy()
    show_item_details(selected_item)

def update_seat(seat, new_description, popup):
    seat.description = new_description
    selected_item = seat
    popup.destroy()
    show_item_details(selected_item)

def delete_sector(sector, popup):
    """Delete the sector from the list and the canvas."""
    sectors.remove(sector)
    canvas.delete("all")
    # Redraw the sectors and seats
    for sector in sectors:
        if sector.is_stage:
            canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                    fill="gray", outline="", width=2)
        else:
            canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                    fill="", outline="red", width=2)
        for seat in sector.seats:
            canvas.create_oval(seat.x - SEAT_CIRCLE_RANGE,
                                seat.y - SEAT_CIRCLE_RANGE,
                                  seat.x + SEAT_CIRCLE_RANGE,
                                    seat.y + SEAT_CIRCLE_RANGE, fill="red")
    popup.destroy()

def delete_seat(seat, popup):
    """Delete the seat from the sector and the canvas."""
    for sector in sectors:
        if seat in sector.seats:
            sector.seats.remove(seat)
            canvas.delete("all")
            # Redraw the sectors and seats
            for sector in sectors:
                if sector.is_stage:
                    canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                            fill="gray", outline="", width=2)
                else:
                    canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                            fill="", outline="red", width=2)
                for seat in sector.seats:
                    canvas.create_oval(seat.x - SEAT_CIRCLE_RANGE,
                                        seat.y - SEAT_CIRCLE_RANGE,
                                          seat.x + SEAT_CIRCLE_RANGE,
                                            seat.y + SEAT_CIRCLE_RANGE, fill="red")
            break
    popup.destroy()

def save_map():
    filename = filedialog.asksaveasfilename(defaultextension=".json", filetypes=[("JSON files", "*.json")])
    if filename:
        map_data = []
        for sector in sectors:
            sector_data = {
                "name": sector.name,
                "x_sx": sector.x_sx,
                "y_sx": sector.y_sx,
                "x_dx": sector.x_dx,
                "y_dx": sector.y_dx,
                "is_stage": sector.is_stage,
                "seats": [{"description": seat.description, "x": seat.x, "y": seat.y} for seat in sector.seats]
            }
            map_data.append(sector_data)
        
        with open(filename, 'w') as f:
            json.dump(map_data, f, indent=4)

def load_map():
    # Clear existing sectors and canvas
    global sectors
    sectors = []
    canvas.delete("all")  # Clear the canvas

    filename = filedialog.askopenfilename(defaultextension=".json", filetypes=[("JSON files", "*.json")])
    if filename:
        with open(filename, 'r') as f:
            map_data = json.load(f)
            for sector_data in map_data:
                sector = Sector(
                    sector_data["name"], sector_data["x_sx"], sector_data["y_sx"],
                    sector_data["x_dx"], sector_data["y_dx"], sector_data["is_stage"]
                )
                for seat_data in sector_data["seats"]:
                    seat = Seat(seat_data["description"], seat_data["x"], seat_data["y"])
                    sector.seats.append(seat)
                sectors.append(sector)
                # Drawing the sectors and seats
                if sector.is_stage:
                    canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                            fill="gray", outline="", width=2)  # No red border for stage
                else:
                    canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                            fill="", outline="red", width=2)  # Red border for non-stage sectors
                for seat in sector.seats:
                    canvas.create_oval(seat.x - SEAT_CIRCLE_RANGE,
                                        seat.y - SEAT_CIRCLE_RANGE,
                                          seat.x + SEAT_CIRCLE_RANGE,
                                            seat.y + SEAT_CIRCLE_RANGE, fill="red")

def add_seats_to_sector(sector, num_seats_str, numbering=False):
    """Automatically add a number of seats to the given sector with optional numbering."""
    try:
        sector.seats = []
        num_seats = int(num_seats_str)  # Convert input to an integer
        if num_seats <= 0:
            raise ValueError("Number of seats must be greater than 0.")
        
        # Calculate available width and height of the sector for placing seats
        available_width = sector.x_dx - sector.x_sx
        available_height = sector.y_dx - sector.y_sx
        
        # Add a smaller margin to avoid seats touching the borders of the sector
        margin = 10  # Reduced margin to 10 to bring seats closer to the borders
        available_width -= 2 * margin
        available_height -= 2 * margin
        
        # Calculate the number of seats that can fit based on the available space
        seats_per_row = available_width // (2 * SEAT_CIRCLE_RANGE)  # Horizontal seats per row
        max_rows = available_height // (2 * SEAT_CIRCLE_RANGE)  # Vertical rows of seats
        
        total_seats_that_fit = seats_per_row * max_rows
        
        if num_seats > total_seats_that_fit:
            raise ValueError(f"Not enough space to add {num_seats} seats. Only {total_seats_that_fit} can fit in this sector.")
        
        # Calculate the spacing for each seat
        x_spacing = available_width // seats_per_row  # Horizontal spacing between seats
        y_spacing = available_height // max_rows  # Vertical spacing between seats

        # Adjust spacing to make sure the seats don't overflow
        remaining_space_x = available_width - (seats_per_row * x_spacing)
        remaining_space_y = available_height - (max_rows * y_spacing)
        
        x_spacing += remaining_space_x // seats_per_row
        y_spacing += remaining_space_y // max_rows

        # Add the seats in a grid-like fashion
        for i in range(num_seats):
            row = i // seats_per_row
            col = i % seats_per_row
            x = sector.x_sx + margin + col * x_spacing + SEAT_CIRCLE_RANGE
            y = sector.y_sx + margin + row * y_spacing + SEAT_CIRCLE_RANGE
            seat = Seat(description="Unnumbered", x=x, y=y)
            if numbering:
                seat.description = "Seat " + str(i + 1)  # Number seats sequentially starting from 1
            sector.seats.append(seat)
        
        canvas.delete("all")
        # Redraw the sectors and seats
        for sector in sectors:
            if sector.is_stage:
                canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                        fill="gray", outline="", width=2)
            else:
                canvas.create_rectangle(sector.x_sx, sector.y_sx, sector.x_dx, sector.y_dx, 
                                        fill="", outline="red", width=2)
            for seat in sector.seats:
                canvas.create_oval(seat.x - SEAT_CIRCLE_RANGE,
                                    seat.y - SEAT_CIRCLE_RANGE,
                                    seat.x + SEAT_CIRCLE_RANGE,
                                        seat.y + SEAT_CIRCLE_RANGE, fill="red")
    except ValueError as e:
        print(f"Error: {e}")
        tk.messagebox.showerror("Error", str(e))

root = tk.Tk()
root.title("Seat Chart Editor")

main_frame = tk.Frame(root)
main_frame.pack(fill="both", expand=True)

canvas = tk.Canvas(main_frame, bg="white", width=700, height=700)
canvas.pack(side="left", fill="both", expand=True)
canvas.bind("<Button-1>", on_canvas_click)
canvas.bind("<Button-3>", on_canvas_right_click)

menu_frame = tk.Frame(main_frame, width=200, bg="lightgray")
menu_frame.pack(side="right", fill="y")
menu_frame.pack_propagate(False)

menu_buttons = [
    tk.Button(menu_frame, text="Stage", command=lambda: set_tool('stage'), width=20, height=2),
    tk.Button(menu_frame, text="Sector", command=lambda: set_tool('sector'), width=20, height=2),
    tk.Button(menu_frame, text="Seat", command=lambda: set_tool('seat'), width=20, height=2)
]

for button in menu_buttons:
    button.pack(pady=10, padx=10)

save_button = tk.Button(menu_frame, text="Save Map", command=save_map, width=20, height=2)
save_button.pack(pady=10, padx=10)

load_button = tk.Button(menu_frame, text="Load Map", command=load_map, width=20, height=2)
load_button.pack(pady=10, padx=10)

root.mainloop()
