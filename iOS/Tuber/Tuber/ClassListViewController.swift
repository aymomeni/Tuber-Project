//
//  ClassListViewController.swift
//  Tuber
//
//  Created by Anne on 12/6/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ClassListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var classTableView: UITableView!
    
//    var classes = ["CS 4400", "CS 3100", "CS 4150"]
    var classes = UserDefaults.standard.object(forKey: "userStudentCourses") as! Array<String>
    
    override func viewDidLoad() {
        super.viewDidLoad()

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //TODO: DB query, how many classes enrolled
        return classes.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ClassTableViewCell
        
        //TODO: DB query,
        cell.classNameLabel.text = classes[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        let currentCell = tableView.cellForRow(at: indexPath!)! as! ClassTableViewCell
        UserDefaults.standard.set(currentCell.classNameLabel.text! as String?, forKey: "selectedClass")
        selectedClass.className = currentCell.classNameLabel.text!
        performSegue(withIdentifier: "selectClass", sender: nil)
    }
    
    struct selectedClass {
        static var className = String()
    }
    
}
