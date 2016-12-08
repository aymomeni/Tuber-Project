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
    
    var classes = ["CS 4400", "CS 3100", "CS 4150"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "selectClass"
        {
            print(sender as? String)
            if let destination = segue.destination as? ClassOptionsViewController
            {
                //destination.passed = sender as? String
                destination.title = sender as? String
            }
        }
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! ClassTableViewCell
        
        cell.classNameLabel.text = classes[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! ClassTableViewCell
        
//        let toPass = currentCell.textLabel!.text
        let toPass = currentCell.classNameLabel.text
        
        print(toPass)
        
        selectedClass.className = toPass!
        
        performSegue(withIdentifier: "selectClass", sender: toPass)
    }
    
    struct selectedClass {
        static var className = String()
    }
    
}
